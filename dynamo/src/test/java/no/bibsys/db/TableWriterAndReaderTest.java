package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IoTestUtils;
import org.junit.Before;
import org.junit.Test;


public class TableWriterAndReaderTest extends LocalDynamoTest implements IoTestUtils {


    private TableReader tableReader;
    private TableWriter tableWriter;
    private TableCreator tableCreator;


    @Override
    @Before
    public void init() {
        super.init();
        tableReader = new TableReader(TableDriver.create(localClient, new DynamoDB(localClient)),
            tableName);
        tableWriter = new TableWriter(TableDriver.create(localClient, new DynamoDB(localClient)),
            tableName);
        tableCreator = new TableCreator(TableDriver.create(localClient, new DynamoDB(localClient)));

    }


    @Test
    public void insertJson() throws IOException, InterruptedException {

        String json = resourceAsString(Paths.get("json", "sample.json"));
        Item inputItem = Item.fromJSON(json);
        tableCreator.createTable(tableName);
        tableWriter.insertJson(json);
        String output = tableReader.getEntry("id01");
        Item outputItem = Item.fromJSON(output);
        assertThat(outputItem, is(equalTo(inputItem)));

    }


}
