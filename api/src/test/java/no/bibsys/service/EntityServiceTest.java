package no.bibsys.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;

import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.aws.tools.Environment;
import no.bibsys.aws.tools.IoUtils;
import no.bibsys.db.EntityManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.db.exceptions.RegistryCreationFailureException;
import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;
import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.service.exceptions.UnknownStatusException;
import no.bibsys.service.exceptions.ValidationSchemaNotFoundException;
import no.bibsys.testtemplates.SampleData;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;

public class EntityServiceTest {

    private static final String REGISTRY_ID = "registryId";
    private static final String SCHACL_VALIDATION_SCHEMA_JSON = "validShaclValidationSchema.json";
    private static final String VALIDATION_FOLDER = "validation";
    private final transient SampleData sampleData = new SampleData();
    private final transient AmazonDynamoDB client = LocalDynamoDBHelper.getClient();
    private transient RegistryDto registryDto;
    private transient EntityService entityService;
    private transient RegistryService registryService;

    //necessary for running tests inside IDE
    @BeforeClass
    public static void initLocalDB() {
        System.setProperty("sqlite4java.library.path", "build/libs");
    }

    @Before
    public void init() throws IOException, RegistryMetadataTableBeingCreatedException,
            SettingValidationSchemaUponCreationException, RegistryCreationFailureException, UnknownStatusException {
        Environment environment = Mockito.mock(Environment.class);
        
        AWSResourceGroupsTaggingAPI mockTaggingClient = Mockito.mock(AWSResourceGroupsTaggingAPI.class); 
        AWSLambda mockLambdaClient = Mockito.mock(AWSLambda.class);
        
        GetResourcesResult mockResourcesResult = Mockito.mock(GetResourcesResult.class);
        @SuppressWarnings("unchecked")
        List<ResourceTagMapping> mockGetResourceTagMappingList = mock(List.class);
        ResourceTagMapping mockResourceTagMapping = mock(ResourceTagMapping.class);
        when(mockGetResourceTagMappingList.get(anyInt())).thenReturn(mockResourceTagMapping);
        when(mockResourceTagMapping.getResourceARN()).thenReturn("arn:fake");
        when(mockGetResourceTagMappingList.size()).thenReturn(1);
        when(mockTaggingClient.getResources(any(GetResourcesRequest.class))).thenReturn( mockResourcesResult);
        when(mockResourcesResult.getResourceTagMappingList()).thenReturn(mockGetResourceTagMappingList);

        RegistryManager registyManager = new RegistryManager(client, mockTaggingClient, mockLambdaClient);
        when(environment.readEnv(anyString())).thenAnswer(input -> input.getArgument(0));
        AuthenticationService authenticationService = new AuthenticationService(client, environment);

        registryService = new RegistryService(registyManager, authenticationService, environment);

        authenticationService.createApiKeyTable();
        registryDto = new SampleData().sampleRegistryDto(REGISTRY_ID);
        registryService.createRegistry(registryDto);
        

        EntityManager entityManager = new EntityManager(client, mockTaggingClient, mockLambdaClient);
        entityService = new EntityService(entityManager, registryService);
    }

    @Test(expected = ValidationSchemaNotFoundException.class)
    public void addEntity_NoValidationSchema_throwsException()
            throws IOException, EntityFailedShaclValidationException, ValidationSchemaNotFoundException {
        EntityDto entityDto = sampleData.sampleEntityDtoWithValidData();
        entityService.addEntity(registryDto.getId(), entityDto);
    }

    @Test(expected = EntityFailedShaclValidationException.class)
    public void addEntity_newInvalidEntity_throwsException()
            throws IOException, EntityFailedShaclValidationException, ValidationSchemaNotFoundException,
            ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        addValidationSchemaToRegistry(registryDto.getId());
        EntityDto entityDto = sampleData.sampleEntityDtoWithInValidData();
        entityService.addEntity(registryDto.getId(), entityDto);
    }

    private void addValidationSchemaToRegistry(String registryId)
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        String validationsSchema = IoUtils.resourceAsString(
                Paths.get(VALIDATION_FOLDER, SCHACL_VALIDATION_SCHEMA_JSON));
        registryService.updateRegistrySchema(registryId, validationsSchema);
    }

    @Test
    public void addEntity_newValidEntity_registryWithNewEntity()

            throws IOException, EntityFailedShaclValidationException, ValidationSchemaNotFoundException,
            ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        EntityDto expectedEntity = sampleData.sampleEntityDtoWithValidData();
        addValidationSchemaToRegistry(registryDto.getId());
        entityService.addEntity(registryDto.getId(), expectedEntity);

        EntityDto actualEntity = entityService.getEntity(registryDto.getId(), expectedEntity.getId());
        assertThat(actualEntity.isIsomorphic(expectedEntity), is(equalTo(true)));
    }

}
