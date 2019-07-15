package no.bibsys.db.helpers;

import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;

import java.util.List;

import static java.util.Objects.isNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AwsResourceGroupsTaggingApiMock {

    private static final String EMPTY_RESOURCE_TAG_MAPPING_LIST_ERROR =
            "The resource tag mapping list has not been set";
    private transient List<ResourceTagMapping> resourceTagMappingList;
    private transient GetResourcesRequest getResourcesRequest;

    public void setGetResourcesRequest(GetResourcesRequest getResourcesRequest) {
        this.getResourcesRequest = getResourcesRequest;
    }

    public void setResourceTagMapping(List<ResourceTagMapping> resourceTagMappingList) {
        this.resourceTagMappingList = resourceTagMappingList;
    }

    public AWSResourceGroupsTaggingAPI initialize() {

        if (resourceTagMappingList.isEmpty()) {
            throw new RuntimeException(EMPTY_RESOURCE_TAG_MAPPING_LIST_ERROR);
        }

        GetResourcesResult getResourcesResultMock = new GetResourcesResult();
        getResourcesResultMock.setResourceTagMappingList(resourceTagMappingList);

        AWSResourceGroupsTaggingAPI mockTaggingClient = mock(AWSResourceGroupsTaggingAPI.class);
        GetResourcesResult mockResourcesResult = mock(GetResourcesResult.class);
        if (isNull(getResourcesRequest)) {
            when(mockTaggingClient.getResources(any())).thenReturn(getResourcesResultMock);
        } else {
            when(mockTaggingClient.getResources(getResourcesRequest)).thenReturn(getResourcesResultMock);
        }
        when(mockResourcesResult.getResourceTagMappingList()).thenReturn(resourceTagMappingList);
        return mockTaggingClient;
    }
}
