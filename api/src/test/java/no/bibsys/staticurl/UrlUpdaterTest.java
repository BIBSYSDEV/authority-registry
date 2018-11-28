package no.bibsys.staticurl;

import static org.junit.Assert.assertTrue;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import java.util.Optional;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.git.github.GitInfoImpl;
import org.junit.Test;
import org.mockito.Mockito;

public class UrlUpdaterTest {

    private final transient String branchName = "TheBranch";
    private final transient String ownerNamer = "TheOwner";
    private final transient String repository = "TheRepo";
    private final transient String certificateArn = "TheCerificate";

    private transient UrlUpdater urlUpdater;


    public UrlUpdaterTest() {
        GitInfoImpl gitInfo = new GitInfoImpl(ownerNamer, repository, branchName);
        AmazonApiGateway client = mockClient();
        urlUpdater = new UrlUpdater(
            UrlUpdater.DEFAULT_ZONE_NAME,
            UrlUpdater.DEFAULT_RECORD_SET_NAME,
            gitInfo,
            client,
            certificateArn
        );


    }


    private AmazonApiGateway mockClient() {
        return Mockito.mock(AmazonApiGateway.class);
    }

    @Test
    public void urlUpdater_stage_restApiId() {

        Optional<ChangeResourceRecordSetsRequest> result = urlUpdater
            .updateUrlMapping(Stage.TEST, "SomeAPi");

        assertTrue(result.isPresent());

    }

}
