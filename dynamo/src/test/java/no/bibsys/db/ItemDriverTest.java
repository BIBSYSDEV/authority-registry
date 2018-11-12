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
    public void addItemAndGetItemToExistingTableItemExistsInTable() throws IOException {

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
    public void addItemToNonExistingTableReturnsFalse() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        boolean addItem = itemDriver.addItem("nonExistingTable", json);
        assertThat(addItem, equalTo(false));
    }
    
    @Test 
    public void getItemToNonExistingTableReturnsFalse() throws IOException {
        Optional<String> addItem = itemDriver.getItem("nonExistingTable", "id01");
        assertThat(addItem.isPresent(), equalTo(false));
    }

    @Test 
    public void deleteItemWithExistingItemRemovesItemFromTable() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        itemDriver.deleteItem(template.getId(), "id01");

        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(false));
    }
    
    @Test 
    public void deleteItemWithNonExistingItemReturnsFalse() throws IOException {
        tableDriver.createTable(template.getId());
        boolean deleteItem = itemDriver.deleteItem(template.getId(), "id01");
        
        assertThat(deleteItem, equalTo(false));
    }
    
    @Test
    public void updateItemWithExistingItemUpdatesValuesInItem() throws IOException {
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
    public void updateItemWithNonExistingItemReturnsFalse() throws IOException {
        tableDriver.createTable(template.getId());
        
        String updateJson = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        String updatedLabel = "The updated label";
        updateJson = updateJson.replace("The label", updatedLabel);
        updateJson = updateJson.replace("ID01", "notAnExistingId");
        itemDriver.updateItem(template.getId(), updateJson);
        
        Optional<String> item = itemDriver.getItem(template.getId(), "id01");
        assertThat(item.get().contains(updatedLabel), equalTo(false));
    }
    
    @Test
    public void itemExistsWithExistingItemReturnsTrue() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        
        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(true));
    }
    
    @Test
    public void itemExistsWithExistingItemReturnsFalse() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        
        boolean itemExists = itemDriver.itemExists(template.getId(), "nonExistingId");
        assertThat(itemExists, equalTo(false));
    }
}
