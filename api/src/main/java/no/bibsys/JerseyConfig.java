package no.bibsys;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.bibsys.db.EntityManager;
import no.bibsys.db.ItemDriver;
import no.bibsys.db.ItemManager;
import no.bibsys.db.RegistryManager;
import no.bibsys.db.TableDriver;
import no.bibsys.db.TableManager;
import no.bibsys.service.AuthenticationService;
import no.bibsys.web.DatabaseResource;
import no.bibsys.web.PingResource;
import no.bibsys.web.exception.BadRequestExceptionMapper;
import no.bibsys.web.exception.ConditionalCheckFailedExceptionMapper;
import no.bibsys.web.exception.RegistryNotFoundExceptionMapper;
import no.bibsys.web.exception.TableAlreadyExistsExceptionMapper;
import no.bibsys.web.security.AuthenticationFilter;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this(DynamoDBHelper.getClient(), new EnvironmentReader());
    }

    public JerseyConfig(AmazonDynamoDB client, EnvironmentReader environmentReader) {        
        super();

        TableManager tableManager = new TableManager(TableDriver.create(client, new DynamoDB(client)));
        ItemManager itemManager = new ItemManager(ItemDriver.create(new DynamoDB(client)));
        EntityManager entityManager = new EntityManager(tableManager, itemManager);
        RegistryManager registryManager = new RegistryManager(tableManager, itemManager);
        
        register(new DatabaseResource(registryManager, entityManager));
        register(PingResource.class);

        register(SecurityEntityFilteringFeature.class);
        register(JacksonFeature.class);
        
        register(new AuthenticationFilter(new AuthenticationService(client, environmentReader)));
        
        register(BadRequestExceptionMapper.class);
        register(ConditionalCheckFailedExceptionMapper.class);
        register(TableAlreadyExistsExceptionMapper.class);
        register(RegistryNotFoundExceptionMapper.class);
        
        register(OpenApiResource.class);
        register(AcceptHeaderOpenApiResource.class);
    }

}