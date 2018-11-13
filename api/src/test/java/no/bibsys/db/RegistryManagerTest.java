package no.bibsys.db;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import no.bibsys.db.structures.EntityRegistryTemplate;
import no.bibsys.testtemplates.LocalDynamoTest;
import no.bibsys.testtemplates.SampleData.Entry;
import org.junit.Test;


public class RegistryManagerTest extends LocalDynamoTest {

    private EntityRegistryTemplate createTestEditRequest(String tableName) {
        EntityRegistryTemplate newCreateRequest = new EntityRegistryTemplate();
        newCreateRequest.setId(tableName);
        newCreateRequest.getMetadata().setContributor(Arrays.asList("contributor1", "contributor2"));
        newCreateRequest.getMetadata().setCreator(Arrays.asList("creator1", "creator2"));
        newCreateRequest.getMetadata().setLabel(Arrays.asList("label1", "label2"));
        newCreateRequest.getMetadata().setSameAs(Arrays.asList("sameAs1", "sameAs2"));
        newCreateRequest.getMetadata().setDescription("description");
        newCreateRequest.getMetadata().setLicense("license");
        
        return newCreateRequest;
    }
    
    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists()
        throws IOException {

        String tableName = "createARegistry";
        boolean existsBeforeCreation = registryManager.registryExists(tableName);
        
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        
        registryManager.createRegistryFromTemplate(createRequest);
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertFalse(existsBeforeCreation);
        assertTrue(existsAfterCreation);
    }
    
    @Test
    public void updateMetadata_RegistryExisting_MetadataUpdated() throws IOException {
        
        String tableName = "addMetadataRegistry";

        EntityRegistryTemplate testEditRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(testEditRequest);
        registryManager.updateRegistryMetadata(testEditRequest);
        EntityRegistryTemplate metadata = registryManager.getRegistryMetadata(tableName); 
        
        assertThat(metadata.getId(), is(equalTo(tableName)));

        assertThat(metadata.getMetadata().getCreator().get(0), is(equalTo("creator1")));
        assertThat(metadata.getMetadata().getCreator().get(1), is(equalTo("creator2")));
        assertThat(metadata.getMetadata().getContributor().get(0), is(equalTo("contributor1")));
        assertThat(metadata.getMetadata().getContributor().get(1), is(equalTo("contributor2")));
        assertThat(metadata.getMetadata().getLabel().get(0), is(equalTo("label1")));
        assertThat(metadata.getMetadata().getLabel().get(1), is(equalTo("label2")));
        assertThat(metadata.getMetadata().getSameAs().get(0), is(equalTo("sameAs1")));
        assertThat(metadata.getMetadata().getSameAs().get(1), is(equalTo("sameAs2")));
        assertThat(metadata.getMetadata().getDescription(), is(equalTo("description")));
        assertThat(metadata.getMetadata().getLicense(), is(equalTo("license")));
        assertThat(metadata.getMetadata().getCreateDate().getTime(), is(greaterThan(0L)));
        
    }


    @Test
    public void createRegistry_RegistryAlreadyExists_ReturnsFalse() throws JsonProcessingException {

        String tableName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager.registryExists(tableName );
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest );
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        boolean createRegistryFromTemplate = registryManager.createRegistryFromTemplate(createRequest);
        assertThat(createRegistryFromTemplate, equalTo(false));
    }
    
    @Test
    public void emptyRegistry_RegistryExists_RegistryIsEmpty() throws IOException {
        
        String tableName = "emptyRegistry";
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest);
        Entry entry = sampleData.sampleEntry();
        Optional<String> entityId = entityManager.addEntity(tableName , entry.jsonString());
        boolean entityExists = entityManager.entityExists(tableName, entityId.get());
        assertThat(entityExists, equalTo(true));
        
        registryManager.emptyRegistry(tableName);
        boolean entityExistAfterEmpty = entityManager.entityExists(tableName, entityId.get());
        assertThat(entityExistAfterEmpty, equalTo(false));
    }
    
    @Test
    public void createRegistryFromTemplate_RegistryDoesNotExist_RegistryExists() throws IOException, InterruptedException {
        String tableName = "addSchemaToRegistry";
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest);
        String schemaAsJson = "JSON validation schema";
        registryManager.setSchemaJson(tableName, schemaAsJson);
        
        assertThat(registryManager.getSchemaAsJson(tableName).get(), is(equalTo(schemaAsJson)));
    }
}
