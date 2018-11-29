package no.bibsys.db;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.exceptions.RegistryAlreadyExistsException;
import no.bibsys.db.structures.Entity;
import no.bibsys.db.structures.Entity;
import no.bibsys.testtemplates.LocalDynamoTest;


public class RegistryManagerTest extends LocalDynamoTest {

    private EntityRegistryTemplate createTestEditRequest(String tableName) {
        EntityRegistryTemplate newCreateRequest = new EntityRegistryTemplate();
        newCreateRequest.setId(tableName);
        newCreateRequest.getMetadata()
                .setContributor(Arrays.asList("contributor1", "contributor2"));
        newCreateRequest.getMetadata().setCreator(Arrays.asList("creator1", "creator2"));
        newCreateRequest.getMetadata().setLabel(Arrays.asList("label1", "label2"));
        newCreateRequest.getMetadata().setSameAs(Arrays.asList("sameAs1", "sameAs2"));
        newCreateRequest.getMetadata().setDescription("description");
        newCreateRequest.getMetadata().setLicense("license");

        return newCreateRequest;
    }

    @Test
    public void createRegistry_RegistryNotExisting_RegistryExists() throws IOException {

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

        String registryName = "addMetadataRegistry";

        EntityRegistryTemplate testEditRequest = createTestEditRequest(registryName);
        registryManager.createRegistryFromTemplate(testEditRequest);
        registryManager.updateRegistryMetadata(registryName, testEditRequest);
        EntityRegistryTemplate metadata = registryManager.getRegistryMetadata(registryName);

        assertThat(metadata.getId(), is(equalTo(registryName)));

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


    @Test(expected = RegistryAlreadyExistsException.class)
    public void createRegistry_RegistryAlreadyExists_ThrowsException()
            throws JsonProcessingException {

        String tableName = "tableAlreadyExists";
        boolean existsBeforeCreation = registryManager.registryExists(tableName);
        assertThat("The table should not exist before creation", existsBeforeCreation,
                is(equalTo(false)));
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest);
        boolean existsAfterCreation = registryManager.registryExists(tableName);
        assertThat("The table should  exist before creation", existsAfterCreation,
                is(equalTo(true)));

        registryManager.createRegistryFromTemplate(createRequest);
    }

    @Test
    public void emptyRegistry_RegistryExists_RegistryIsEmpty() throws IOException {

        String tableName = "emptyRegistry";
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest);
        Entity entity = sampleData.sampleEntity();
        Entity addEntity = entityManager.addEntity(tableName, entity.getBodyAsJson());
        boolean entityExists = entityManager.entityExists(tableName, addEntity.getId());
        assertThat(entityExists, equalTo(true));

        registryManager.emptyRegistry(tableName);
        boolean entityExistAfterEmpty = entityManager.entityExists(tableName, addEntity.getId());
        assertThat(entityExistAfterEmpty, equalTo(false));
    }

    @Test
    public void createRegistryFromTemplate_RegistryDoesNotExist_RegistryExists()
            throws IOException, InterruptedException {
        String tableName = "addSchemaToRegistry";
        EntityRegistryTemplate createRequest = createTestEditRequest(tableName);
        registryManager.createRegistryFromTemplate(createRequest);
        String schemaAsJson = "JSON validation schema";
        registryManager.setSchemaJson(tableName, schemaAsJson);

        assertThat(registryManager.getSchemaAsJson(tableName).get(), is(equalTo(schemaAsJson)));
    }
}
