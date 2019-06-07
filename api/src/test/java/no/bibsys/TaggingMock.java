package no.bibsys;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetTagKeysRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetTagKeysResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetTagValuesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetTagValuesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.TagResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.TagResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.UntagResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.UntagResourcesResult;

public class TaggingMock implements AWSResourceGroupsTaggingAPI {

    @Override
    public GetResourcesResult getResources(GetResourcesRequest getResourcesRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetTagKeysResult getTagKeys(GetTagKeysRequest getTagKeysRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetTagValuesResult getTagValues(GetTagValuesRequest getTagValuesRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TagResourcesResult tagResources(TagResourcesRequest tagResourcesRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UntagResourcesResult untagResources(UntagResourcesRequest untagResourcesRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

}
