package no.bibsys.staticurl;

import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import java.util.Optional;
import no.bibsys.aws.cloudformation.Stage;
import no.bibsys.aws.git.github.GitInfo;
import no.bibsys.aws.route53.Route53Updater;
import no.bibsys.aws.route53.StaticUrlInfo;

public class UrlUpdater {


    public final static String DEFAULT_ZONE_NAME = "aws.unit.no";
    public final static String DEFAULT_RECORD_SET_NAME = "api.entitydata.aws.unit.no.";

    private final transient String zoneName;
    private final transient String recordSetName;
    private final transient GitInfo gitInfo;
    private final transient AmazonApiGateway apiGatewayClient;
    private final transient String certificateArn;

    public UrlUpdater(String zoneName,
        String recordSetName,
        GitInfo gitInfo,
        AmazonApiGateway apiGatewayClient,
        String certificateArn) {
        this.zoneName = zoneName;
        this.recordSetName = recordSetName;
        this.gitInfo = gitInfo;
        this.apiGatewayClient = apiGatewayClient;
        this.certificateArn = certificateArn;
    }

    private StaticUrlInfo staticUrlInfo(Stage stage) {
        return StaticUrlInfo.create(stage, zoneName, recordSetName);
    }

    public Optional<ChangeResourceRecordSetsRequest> updateUrlMapping(Stage stage,
        String restApiPhysicalId) {

        Route53Updater route53Updater = new Route53Updater(
            staticUrlInfo(stage),
            gitInfo,
            stage,
            restApiPhysicalId,
            apiGatewayClient
        );

        return route53Updater.createUpdateRequest(certificateArn);


    }



}
