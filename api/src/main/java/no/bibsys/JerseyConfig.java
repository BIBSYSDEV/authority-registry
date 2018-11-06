package no.bibsys;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.bibsys.db.DatabaseManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.PingResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.TableAlreadyExistsExceptionMapper;
import no.bibsys.web.exception.TableNotFoundExceptionMapper;
import no.bibsys.web.security.AuthenticationFilter;

public class JerseyConfig extends ResourceConfig {

    private static final TableDriver TABLE_DRIVER = LocalDynamoDBHelper.getTableDriver();

    public JerseyConfig() {
        this(new DatabaseManager(TABLE_DRIVER), new RegistryManager(TABLE_DRIVER), new EnvironmentReader());
    }

    public JerseyConfig(DatabaseManager databaseManager, RegistryManager registryManager, EnvironmentReader environmentReader) {
        super();

        register(new DatabaseResource(databaseManager, registryManager));
        register(PingResource.class);

        register(SecurityEntityFilteringFeature.class);
        register(JacksonFeature.class);

        register(new AuthenticationFilter(environmentReader));
        
        register(BadRequestExceptionMapper.class);
        register(ConditionalCheckFailedExceptionMapper.class);
        register(TableAlreadyExistsExceptionMapper.class);
        register(TableNotFoundExceptionMapper.class);
        
        register(OpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);
    }

}