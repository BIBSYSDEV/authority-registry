package no.bibsys.staticurl;

import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import java.util.Optional;
import no.bibsys.aws.route53.Route53Updater;

public class UrlUpdater {





    private final transient Route53Updater route53Updater;


    public UrlUpdater(
        Route53Updater route53Updater) {

        this.route53Updater = route53Updater;

    }

    public Optional<ChangeResourceRecordSetsRequest> createUpdateRequest(String certificateArn) {
        return route53Updater.createUpdateRequest(certificateArn);

    }

    public ChangeResourceRecordSetsResult executeUpdate(ChangeResourceRecordSetsRequest request) {
        return route53Updater.executeUpdateRequest(request);
    }


    public Optional<ChangeResourceRecordSetsRequest> createDeleteRequest(){
            return route53Updater.createDeleteRequest();
    }

    public ChangeResourceRecordSetsResult executeDelete(ChangeResourceRecordSetsRequest request) {
        return route53Updater.executeDeleteRequest(request);
    }



}
