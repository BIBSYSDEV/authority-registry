package no.bibsys.db.helpers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;

public class AwsResourceGroupsTaggingApiMock  {

    public static AWSResourceGroupsTaggingAPI build() {
        
        AWSResourceGroupsTaggingAPI mockTaggingClient = mock(AWSResourceGroupsTaggingAPI.class); 
        GetResourcesResult mockResourcesResult = mock(GetResourcesResult.class);
        
        @SuppressWarnings("unchecked")
        List<ResourceTagMapping> mockGetResourceTagMappingList = mock(List.class);
        ResourceTagMapping mockResourceTagMapping = mock(ResourceTagMapping.class);
        when(mockGetResourceTagMappingList.get(anyInt())).thenReturn(mockResourceTagMapping);
        when(mockResourceTagMapping.getResourceARN()).thenReturn("arn:fake");
        when(mockGetResourceTagMappingList.size()).thenReturn(1);
        when(mockTaggingClient.getResources(any(GetResourcesRequest.class))).thenReturn(mockResourcesResult);
        when(mockResourcesResult.getResourceTagMappingList()).thenReturn(mockGetResourceTagMappingList);
        
        return mockTaggingClient;
    }
    
}
