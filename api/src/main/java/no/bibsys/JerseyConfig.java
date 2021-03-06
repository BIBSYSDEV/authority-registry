package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIClientBuilder;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.EntityManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.service.AuthenticationService;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import no.bibsys.service.SearchService;
import no.bibsys.service.exceptions.UnknownStatusExceptionMapper;
import no.bibsys.web.CorsFilter;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.JsonLdContextResource;
import no.bibsys.web.PingResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.BaseExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.EntityNotFoundExceptionMapper;
import no.bibsys.web.exception.ExceptionLogger;
import no.bibsys.web.exception.ForbiddenExceptionMapper;
import no.bibsys.web.exception.IllegalArgumentExceptionMapper;
import no.bibsys.web.exception.RegistryAlreadyExistsExceptionMapper;
import no.bibsys.web.exception.RegistryMetadataTableBeingCreatedExceptionMapper;
import no.bibsys.web.exception.RegistryNotEmptyExceptionMapper;
import no.bibsys.web.exception.RegistryNotFoundExceptionMapper;
import no.bibsys.web.exception.RegistryUnavailableExceptionMapper;
import no.bibsys.web.exception.SettingValidationSchemaUponCreationExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.EntityFailedShaclValidationExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ValidationSchemaNotFoundExceptionMapper;
import no.bibsys.web.exception.validationexceptionmappers.ValidationSchemaSyntaxErrorExceptionMapper;
import no.bibsys.web.model.EntityHtmlMessageBodyWriter;
import no.bibsys.web.model.EntityMarcMessageBodyWriter;
import no.bibsys.web.model.EntityRdfMessageBodyWriter;
import no.bibsys.web.model.RegistryMessageBodyWriter;
import no.bibsys.web.model.RegistryRdfMessageBodyWriter;
import no.bibsys.web.security.AuthenticationFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SuppressWarnings("PMD")
public class JerseyConfig extends ResourceConfig {

    private static Logger logger = LoggerFactory.getLogger(JerseyConfig.class);
    
    public JerseyConfig() {
        this(DynamoDBHelper.getClient(), 
                new Environment(), 
                AWSResourceGroupsTaggingAPIClientBuilder.defaultClient(),
                AWSLambdaClientBuilder.defaultClient());
    }

    public JerseyConfig(AmazonDynamoDB client, 
            Environment environmentReader, 
            AWSResourceGroupsTaggingAPI taggingApiClient,
            AWSLambda lambdaClient) {
        
        super();

        AuthenticationService authenticationService = new AuthenticationService(client, environmentReader);
        RegistryManager registryManager = null;
        try {
            registryManager = new RegistryManager(client, taggingApiClient, lambdaClient);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Could not create RegistryManager");
        }
        RegistryService registryService = new RegistryService(registryManager, authenticationService,
            environmentReader);

        EntityManager entityManager = new EntityManager(client, taggingApiClient, lambdaClient);
        EntityService entityService = new EntityService(entityManager, registryService);
        
        SearchService searchService = new SearchService(environmentReader);

        register(new DatabaseResource(registryService, entityService, searchService));
        register(PingResource.class);

        register(SecurityEntityFilteringFeature.class);
        register(JacksonFeature.class);

        register(new AuthenticationFilter(authenticationService));
        register(CorsFilter.class);
        
        register(ExceptionLogger.class);

        register(OpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);

        registerExceptionMappers();
        registerMessageBodyWriters();

        register(JsonLdContextResource.class);
    }

    private void registerMessageBodyWriters() {
        register(RegistryMessageBodyWriter.class);
        register(RegistryRdfMessageBodyWriter.class);
        register(EntityHtmlMessageBodyWriter.class);
        register(EntityMarcMessageBodyWriter.class);
        register(EntityRdfMessageBodyWriter.class);
    }

    private void registerExceptionMappers() {
        register(BaseExceptionMapper.class);
        register(ForbiddenExceptionMapper.class);
        register(BadRequestExceptionMapper.class);
        register(ConditionalCheckFailedExceptionMapper.class);
        register(RegistryAlreadyExistsExceptionMapper.class);
        register(RegistryNotFoundExceptionMapper.class);
        register(RegistryNotEmptyExceptionMapper.class);
        register(RegistryUnavailableExceptionMapper.class);
        register(EntityNotFoundExceptionMapper.class);
        register(IllegalArgumentExceptionMapper.class);
        register(RegistryMetadataTableBeingCreatedExceptionMapper.class);
        register(EntityFailedShaclValidationExceptionMapper.class);
        register(ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper.class);
        register(ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper.class);
        register(ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper.class);
        register(ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesExceptionMapper.class);
        register(ValidationSchemaSyntaxErrorExceptionMapper.class);
        register(ValidationSchemaNotFoundExceptionMapper.class);
        register(SettingValidationSchemaUponCreationExceptionMapper.class);
        register(UnknownStatusExceptionMapper.class);
    }

}
