package no.bibsys;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import no.bibsys.db.DatabaseManager;
import no.bibsys.web.CustomOpenApiResource;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.HelloResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.TableAlreadyExistsExceptionMapper;
import no.bibsys.web.exception.TableNotFoundExceptionMapper;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this(new DatabaseManager(DynamoDBHelper.getTableDriver()));
    }

    public JerseyConfig(DatabaseManager databaseManager) {
        super();

        DatabaseResource databaseController = new DatabaseResource(databaseManager);

        register(databaseController);
        register(HelloResource.class);

        register(JacksonFeature.class);

        register(BadRequestExceptionMapper.class);
        register(ConditionalCheckFailedExceptionMapper.class);
        register(TableAlreadyExistsExceptionMapper.class);
        register(TableNotFoundExceptionMapper.class);
        
        register(CustomOpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);
    }

}
