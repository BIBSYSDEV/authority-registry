package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;

import no.bibsys.utils.IoUtils;


public class ItemDriverTest extends LocalDynamoTest {


    private static final String NON_EXISTING_ID = "nonExistingId";
    private static final String NON_EXISTING_TABLE = "NonExistingTable";
    private ItemDriver itemDriver;
    private TableDriver tableDriver;

    @Override
    @Before
    public void init() {
        super.init();
        itemDriver = ItemDriver.create(new DynamoDB(localClient));
        tableDriver = TableDriver.create(localClient, new DynamoDB(localClient));
    }

    @Test
    public void addItemAndGetItem_TableExists_ItemExistsInTable() throws IOException {

        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        Item inputItem = Item.fromJSON(json);
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        Optional<String> output = itemDriver.getItem(template.getId(), "id01");
        Optional<Item> outputItem = output.map(i -> Item.fromJSON(i));
        assertThat(outputItem.isPresent(), is(equalTo(true)));
        assertThat(outputItem.get(), is(equalTo(inputItem)));

        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(true));

    }
    
    @Test 
    public void addItem_TableNotExisting_ReturnsFalse() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        boolean addItem = itemDriver.addItem("nonExistingTable", json);
        assertThat(addItem, equalTo(false));
    }
    
    @Test 
    public void getItem_TableNotExisting_ReturnsFalse() throws IOException {
        Optional<String> addItem = itemDriver.getItem("nonExistingTable", "id01");
        assertThat(addItem.isPresent(), equalTo(false));
    }

    @Test 
    public void deleteItem_ItemExists_RemovesItemFromTable() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        itemDriver.deleteItem(template.getId(), "id01");

        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(false));
    }
    
    @Test 
    public void deleteItem_ItemNotExisting_ReturnsFalse() throws IOException {
        tableDriver.createTable(template.getId());
        boolean deleteItem = itemDriver.deleteItem(template.getId(), "id01");
        
        assertThat(deleteItem, equalTo(false));
    }
    
    @Test 
    public void deleteItem_TableNotExisting_ReturnsFalse() throws IOException {
        boolean deleteItem = itemDriver.deleteItem(NON_EXISTING_TABLE, "id01");
        
        assertThat(deleteItem, equalTo(false));
    }
    
    @Test
    public void updateItem_ItemExists_UpdatesValuesInItem() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);

        String updateJson = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        String updatedLabel = "The updated label";
        updateJson = updateJson.replace("The label", updatedLabel);
        itemDriver.updateItem(template.getId(), updateJson);
        
        Optional<String> item = itemDriver.getItem(template.getId(), "id01");
        assertThat(item.get().contains(updatedLabel), equalTo(true));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void updateItem_ItemNotExisting_ReturnsFalse() throws IOException {
        tableDriver.createTable(template.getId());
        
        String updateJson = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        String updatedLabel = "The updated label";
        updateJson = updateJson.replace("The label", updatedLabel);
        updateJson = updateJson.replace("ID01", NON_EXISTING_ID);
        itemDriver.updateItem(template.getId(), updateJson);
        
        Optional<String> item = itemDriver.getItem(template.getId(), "id01");
        assertThat(item.get().contains(updatedLabel), equalTo(false));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void updateItem_TableNotExisting_ReturnsFalse() throws IOException {
        String updateJson = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        String updatedLabel = "The updated label";
        updateJson = updateJson.replace("The label", updatedLabel);
        updateJson = updateJson.replace("ID01", NON_EXISTING_ID);
        itemDriver.updateItem(NON_EXISTING_TABLE, updateJson);
        
        Optional<String> item = itemDriver.getItem(NON_EXISTING_TABLE, "id01");
        assertThat(item.get().contains(updatedLabel), equalTo(false));
    }
    
    @Test
    public void itemExists_ItemExisting_ReturnsTrue() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        
        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(true));
    }
    
    @Test
    public void itemExists_ItemNotExisting_ReturnsFalse() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        
        boolean itemExists = itemDriver.itemExists(template.getId(), NON_EXISTING_ID);
        assertThat(itemExists, equalTo(false));
    }
    
    @Test
    public void itemExists_TableNotExisting_ReturnsFalse() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        itemDriver.addItem(NON_EXISTING_TABLE, json);
        
        boolean itemExists = itemDriver.itemExists(NON_EXISTING_TABLE, NON_EXISTING_ID);
        assertThat(itemExists, equalTo(false));
    }
}
