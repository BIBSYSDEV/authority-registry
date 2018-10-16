package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import no.bibsys.utils.IoTestUtils;
import org.junit.Before;
import org.junit.Test;


public class TableWriterAndReaderTest extends LocalDynamoTest implements IoTestUtils {


    private TableReader tableReader;
    private TableWriter tableWriter;
    private TableManager tableManager;


    private String validationSchema = "validationSchema";

    @Override
    @Before
    public void init() {
        super.init();
        tableReader = new TableReader(TableDriver.create(localClient, new DynamoDB(localClient)),
            tableName);
        tableWriter = new TableWriter(TableDriver.create(localClient, new DynamoDB(localClient)),
            tableName);
        tableManager = new TableManager(TableDriver.create(localClient, new DynamoDB(localClient)));

    }


    @Test
    public void insertJson() throws IOException, InterruptedException {

        String json = resourceAsString(Paths.get("json", "sample.json"));
        Item inputItem = Item.fromJSON(json);
        tableManager.createRegistry(tableName, validationSchema);
        tableWriter.insertJson(json);
        Optional<String> output = tableReader.getEntry("id01");
        Optional<Item> outputItem = output.map(i -> Item.fromJSON(i));
        assertThat(outputItem.isPresent(), is(equalTo(true)));
        assertThat(outputItem.get(), is(equalTo(inputItem)));

    }


}
