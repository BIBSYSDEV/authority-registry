package no.bibsys.db.helpers;

import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import com.amazonaws.services.resourcegroupstaggingapi.model.Tag;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class AwsResourceGroupsTaggingApiMockBuilder {
    private transient final List<ResourceTagMapping> resourceTagMappingList;
    private static final String UNIT_RESOURCE_TYPE = "unit.resource_type";
    private static final String AWS_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    private static final String AWS_CLOUDFORMATION_STACK_NAME = "aws:cloudformation:stack-name";
    private static final String DYNAMO_DB_TRIGGER_EVENT_PROCESSOR = "DynamoDBTrigger_EventProcessor";
    private static final String DYNAMO_DB_EVENT_PROCESSOR_LAMBDA = "DynamoDBEventProcessorLambda";

    public AwsResourceGroupsTaggingApiMockBuilder() {
        resourceTagMappingList = new ArrayList<>();
    }

    public AwsResourceGroupsTaggingApiMockBuilder withRandomUnmatchableResourceTagMapping(int count) {
        int counter = count;
        while (counter > 0) {
            resourceTagMappingList.add(generateRandomResourceTagMapping());
            counter--;
        }
        return this;
    }

    public AwsResourceGroupsTaggingApiMockBuilder withMatchableResourceTagMapping(String stackName) {
        this.resourceTagMappingList.add(generateTagMapping(stackName));
        return this;
    }

    private ResourceTagMapping generateTagMapping(String stackName) {
        ResourceTagMapping resourceTagMapping = new ResourceTagMapping();
        Tag stackNameTag = new Tag().withKey(AWS_CLOUDFORMATION_STACK_NAME).withValue(stackName);
        Tag logicalIdTag = new Tag().withKey(AWS_CLOUDFORMATION_LOGICAL_ID)
                .withValue(AwsResourceGroupsTaggingApiMockBuilder.DYNAMO_DB_EVENT_PROCESSOR_LAMBDA);
        Tag unitResourceTag = new Tag().withKey(UNIT_RESOURCE_TYPE)
                .withValue(AwsResourceGroupsTaggingApiMockBuilder.DYNAMO_DB_TRIGGER_EVENT_PROCESSOR);
        resourceTagMapping.setResourceARN(RandomStringUtils.randomAlphabetic(9));
        return resourceTagMapping.withTags(stackNameTag)
                .withTags(logicalIdTag).withTags(unitResourceTag);
    }

    private ResourceTagMapping generateRandomResourceTagMapping() {
        return generateTagMapping(RandomStringUtils.randomAlphanumeric(9)
        );
    }

    public AwsResourceGroupsTaggingApiMock build() {
        AwsResourceGroupsTaggingApiMock awsResourceGroupsTaggingApiMock = new AwsResourceGroupsTaggingApiMock();
        awsResourceGroupsTaggingApiMock.setResourceTagMapping(this.resourceTagMappingList);
        return awsResourceGroupsTaggingApiMock;
    }
}
