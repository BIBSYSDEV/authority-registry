package no.bibsys.staticurl;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.model.BasePathMapping;
import com.amazonaws.services.apigateway.model.GetBasePathMappingsResult;
import com.amazonaws.services.apigateway.model.GetDomainNameResult;
import com.amazonaws.services.certificatemanager.model.RecordType;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UrlUpdaterTest {

    private static final String DEFAULT_ZONE_NAME = "aws.unit.no.";
    private static final String DEFAULT_RECORD_SET_NAME = "api.entitydata.aws.unit.no.";
    private final transient String certificateArn = "TheCerificate";
    private final transient String hostedZoneId = "HOSTEDZONEID";
    private final transient String domainName = "DomainName";

    private final transient StaticUrlInfo staticUrlInfo;
    private transient UrlUpdater urlUpdater;

    private transient ResourceRecordSet recordSet;
    private Route53Updater route53Updater;
    @Mock
    AmazonRoute53 route53Client;
    private AmazonApiGateway apiGatewayClient;

    public UrlUpdaterTest() {
        staticUrlInfo = new StaticUrlInfo(DEFAULT_ZONE_NAME, DEFAULT_RECORD_SET_NAME, Stage.TEST);
        apiGatewayClient = mockApiGatewayClient();
        route53Updater = new Route53Updater(staticUrlInfo, "apiID", apiGatewayClient, route53Client);

        route53Updater.setRoute53Client(mockRoute53Client());
        urlUpdater = new UrlUpdater(route53Updater);
    }

    @Before
    public void generateRecordSet() {
        Optional<ChangeResourceRecordSetsRequest> requestOpt = urlUpdater.createUpdateRequest(certificateArn);

        assertTrue(requestOpt.isPresent());
        ChangeResourceRecordSetsRequest request = requestOpt.get();
        assertThat(request.getHostedZoneId(), is(equalTo(hostedZoneId)));
        assertThat(request.getChangeBatch().getChanges().size(), is(equalTo(1)));

        this.recordSet = request.getChangeBatch().getChanges().get(0).getResourceRecordSet();
    }

    private AmazonRoute53 mockRoute53Client() {
        AmazonRoute53 client = Mockito.mock(AmazonRoute53.class);
        ListHostedZonesResult mockResult = new ListHostedZonesResult()
                .withHostedZones(new HostedZone().withId(hostedZoneId).withName(DEFAULT_ZONE_NAME));
        when(client.listHostedZones()).thenReturn(mockResult);
        return client;
    }

    private AmazonApiGateway mockApiGatewayClient() {
        AmazonApiGateway client = Mockito.mock(AmazonApiGateway.class);
        when(client.getDomainName(any())).thenReturn(new GetDomainNameResult().withRegionalDomainName(domainName));

        // necessary when apiGateway is called to delete old mappings
        when(client.getBasePathMappings(any()))
            .thenReturn(new GetBasePathMappingsResult().withItems(new BasePathMapping().withBasePath("BasePathValue")));
        return client;
    }

    @Test
    public void createUpdateRequest_stage_RecordSetWithCorrectName() {

        assertThat(recordSet.getName(), is(equalTo(staticUrlInfo.getRecordSetName())));

        String cnameRecord = recordSet.getResourceRecords().get(0).getValue();
        assertThat(cnameRecord, is(equalTo(domainName)));
    }

    @Test
    public void createUpdateRequest_stage_RecordSetWithOneCnameEntry() {

        assertThat(recordSet.getResourceRecords().size(), is(equalTo(1)));
        assertThat(recordSet.getType(), is(equalTo(RecordType.CNAME.name())));
    }

    @Test
    public void createUpdateRequest_stage_RecordSetWithCorrectDomainName() {
        assertThat(recordSet.getResourceRecords().size(), is(equalTo(1)));

        String cnameRecord = recordSet.getResourceRecords().get(0).getValue();
        assertThat(cnameRecord, is(equalTo(domainName)));
    }

    @Test
    public void createDeleteRequest_stage_notNull() {
        Optional<ChangeResourceRecordSetsRequest> deleteRequest = urlUpdater.createDeleteRequest();
        assertThat(deleteRequest.get(), is(not(nullValue())));
    }

    @Test
    public void executeUpdateRequest_stage_notNull() {
        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater.createUpdateRequest(certificateArn);
        ChangeResourceRecordSetsRequest recordSetsRequest = request.get();

        urlUpdater = new UrlUpdater(new MockRoute53Updater(staticUrlInfo, "apiID", apiGatewayClient, route53Client));

        ChangeResourceRecordSetsResult testResult = urlUpdater.executeUpdate(recordSetsRequest);
        assertThat(testResult, is(not(nullValue())));
    }

    @Test
    public void executeDeleteRequest_stage_notNull() {
        Optional<ChangeResourceRecordSetsRequest> request = urlUpdater.createDeleteRequest();
        ChangeResourceRecordSetsRequest recordSetsRequest = request.get();

        urlUpdater = new UrlUpdater(new MockRoute53Updater(staticUrlInfo, "apiID", apiGatewayClient, route53Client));

        ChangeResourceRecordSetsResult result = urlUpdater.executeDelete(recordSetsRequest);
        assertThat(result, is(not(nullValue())));
    }

    static class MockRoute53Updater extends Route53Updater {

        public MockRoute53Updater(StaticUrlInfo staticUrlInfo, String apiGatewayRestApiId,
                                  AmazonApiGateway apiGatewayClient, AmazonRoute53 route53Client) {
            super(staticUrlInfo, apiGatewayRestApiId, apiGatewayClient, route53Client);
        }

        @Override
        public ChangeResourceRecordSetsResult executeDeleteRequest(ChangeResourceRecordSetsRequest request) {
            return new ChangeResourceRecordSetsResult();
        }

        @Override
        public ChangeResourceRecordSetsResult executeUpdateRequest(ChangeResourceRecordSetsRequest request) {
            return new ChangeResourceRecordSetsResult();
        }
    }
}
