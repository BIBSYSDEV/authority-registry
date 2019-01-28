package no.bibsys;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import java.io.IOException;
import no.bibsys.aws.tools.Environment;
import no.bibsys.db.EntityManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.service.AuthenticationService;
import no.bibsys.service.EntityService;
import no.bibsys.service.RegistryService;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.PingResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.BaseExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.EntityNotFoundExceptionMapper;
import no.bibsys.web.exception.ExceptionLogger;
import no.bibsys.web.exception.ForbiddenExceptionMapper;
import no.bibsys.web.exception.IllegalArgumentExceptionMapper;
import no.bibsys.web.exception.ProcessingExceptionMapper;
import no.bibsys.web.exception.RegistryAlreadyExistsExceptionMapper;
import no.bibsys.web.exception.RegistryMetadataTableBeingCreatedExceptionMapper;
import no.bibsys.web.exception.RegistryNotEmptyExceptionMapper;
import no.bibsys.web.exception.RegistryNotFoundExceptionMapper;
import no.bibsys.web.exception.RegistryUnavailableExceptionMapper;
import no.bibsys.web.exception.validationexceptions.EntryFailedShaclValidationExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ShaclModelPropertiesAreNotIcludedInOntologyExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesExceptionMapper;
import no.bibsys.web.exception.validationexceptions.ValidationSchemaSyntaxErrorExceptionMapper;
import no.bibsys.web.model.EntityHtmlMessageBodyWriter;
import no.bibsys.web.model.RegistryMessageBodyWriter;
import no.bibsys.web.model.RegistryMessageJsonBodyWriter;
import no.bibsys.web.security.AuthenticationFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD")
public class JerseyConfig extends ResourceConfig {

    private static Logger logger = LoggerFactory.getLogger(JerseyConfig.class);

    public JerseyConfig() {
        this(DynamoDBHelper.getClient(), new Environment());
    }

    public JerseyConfig(AmazonDynamoDB client, Environment environmentReader) {
        super();

        AuthenticationService authenticationService = new AuthenticationService(client, environmentReader);

        RegistryManager registryManager = null;
        try {
            registryManager = new RegistryManager(client);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Could not create RegistryManager");
        }
        RegistryService registryService = new RegistryService(registryManager, authenticationService,
            environmentReader);

        EntityManager entityManager = new EntityManager(client);
        EntityService entityService = new EntityService(entityManager, registryService);

        register(new DatabaseResource(registryService, entityService));
        register(PingResource.class);

        register(SecurityEntityFilteringFeature.class);
        register(JacksonFeature.class);

        register(new AuthenticationFilter(authenticationService));

        registerExceptionMappers();

        register(ExceptionLogger.class);

        register(OpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);

        register(RegistryMessageBodyWriter.class);
        register(RegistryMessageJsonBodyWriter.class);
        register(EntityHtmlMessageBodyWriter.class);
        register(RegistryMessageBodyWriter.class);
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
        register(ProcessingExceptionMapper.class);
        register(EntryFailedShaclValidationExceptionMapper.class);
        register(ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionMapper.class);
        register(ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper.class);
        register(ShaclModelPropertiesAreNotIcludedInOntologyExceptionMapper.class);
        register(ShaclModelTargetClassesAreNotClassesOfOntologyExceptionMapper.class);
        register(ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesExceptionMapper.class);
        register(ValidationSchemaSyntaxErrorExceptionMapper.class);
    }

}
