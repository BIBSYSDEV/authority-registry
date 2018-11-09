package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Paths;
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
    public void addAndGetItem() throws IOException {

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
    public void deleteItem() throws IOException {
        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        tableDriver.createTable(template.getId());
        itemDriver.addItem(template.getId(), json);
        itemDriver.deleteItem(template.getId(), "id01");

        boolean itemExists = itemDriver.itemExists(template.getId(), "id01");
        assertThat(itemExists, equalTo(false));
    }
    
    @Test
    public void updateItem() throws IOException {
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
}
