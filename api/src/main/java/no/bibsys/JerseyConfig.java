package no.bibsys;

import no.bibsys.aws.tools.Environment;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.bibsys.db.EntityManager;
import no.bibsys.db.ItemDriver;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.service.AuthenticationService;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.PingResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.BaseExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.EntityNotFoundExceptionMapper;
import no.bibsys.web.exception.ExceptionLogger;
import no.bibsys.web.exception.ForbiddenExceptionMapper;
import no.bibsys.web.exception.RegistryAlreadyExistsExceptionMapper;
import no.bibsys.web.exception.RegistryNotEmptyExceptionMapper;
import no.bibsys.web.exception.RegistryNotFoundExceptionMapper;
import no.bibsys.web.exception.RegistryUnavailableExceptionMapper;
import no.bibsys.web.security.AuthenticationFilter;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this(DynamoDBHelper.getClient(), new Environment());
    }

    public JerseyConfig(AmazonDynamoDB client, Environment environmentReader) {
        super();

        TableDriver tableDriver = TableDriver.create(client);
        ItemDriver itemDriver = ItemDriver.create(tableDriver);
        EntityManager entityManager = new EntityManager(itemDriver);
        AuthenticationService authenticationService =
                new AuthenticationService(client, environmentReader);

        RegistryManager registryManager = new RegistryManager(tableDriver, itemDriver,
                authenticationService, environmentReader);

        register(new DatabaseResource(registryManager, entityManager));
        register(PingResource.class);

        register(SecurityEntityFilteringFeature.class);
        register(JacksonFeature.class);

        register(new AuthenticationFilter(authenticationService));

        registerExceptionMappers();

        register(ExceptionLogger.class);

        register(OpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);
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
    }

}
