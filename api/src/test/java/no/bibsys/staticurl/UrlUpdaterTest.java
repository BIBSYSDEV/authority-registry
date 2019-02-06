package no.bibsys.staticurl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.model.BasePathMapping;
import com.amazonaws.services.apigateway.model.GetBasePathMappingsResult;
import com.amazonaws.services.apigateway.model.GetDomainNameResult;
import com.amazonaws.services.certificatemanager.model.RecordType;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import java.util.Optional;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UrlUpdaterTest {

    private static final String DEFAULT_ZONE_NAME = "aws.unit.no";
    private static final String DEFAULT_RECORD_SET_NAME = "api.entitydata.aws.unit.no.";
    private final transient String certificateArn = "TheCerificate";
    private final transient String hostedZoneId = "HOSTEDZONEID";
    private final transient String domainName = "DomainName";

    private final transient StaticUrlInfo staticUrlInfo;
    private transient UrlUpdater urlUpdater;

    private transient ResourceRecordSet recordSet;

    public UrlUpdaterTest() {
        staticUrlInfo = new StaticUrlInfo(DEFAULT_ZONE_NAME, DEFAULT_RECORD_SET_NAME, Stage.TEST);
        AmazonApiGateway apiGatewayClient = mockApiGatewayClient();
        AmazonRoute53 route53Client = Mockito.mock(AmazonRoute53.class);
        Route53Updater route53Updater = new Route53Updater(staticUrlInfo, "apiID", apiGatewayClient, route53Client);

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
}
