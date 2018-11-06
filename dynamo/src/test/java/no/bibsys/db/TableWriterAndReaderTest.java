package no.bibsys.db;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import no.bibsys.utils.IoUtils;
import org.junit.Before;
import org.junit.Test;


public class TableWriterAndReaderTest extends LocalDynamoTest {


    private EntityManager entityManager;
    private TableManager tableManager;

    @Override
    @Before
    public void init() {
        super.init();
        entityManager = new EntityManager(TableDriver.create(localClient, new DynamoDB(localClient)), tableName);
        tableManager = new TableManager(TableDriver.create(localClient, new DynamoDB(localClient)));

    }


    @Test
    public void addJson() throws IOException, InterruptedException {

        String json = IoUtils.resourceAsString(Paths.get("json", "sample.json"));
        Item inputItem = Item.fromJSON(json);
        tableManager.createRegistry(template);
        entityManager.addJson(json);
        Optional<String> output = entityManager.getEntry("id01");
        Optional<Item> outputItem = output.map(i -> Item.fromJSON(i));
        assertThat(outputItem.isPresent(), is(equalTo(true)));
        assertThat(outputItem.get(), is(equalTo(inputItem)));

    }


}
