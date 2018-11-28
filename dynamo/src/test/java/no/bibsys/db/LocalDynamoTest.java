package no.bibsys.db;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import no.bibsys.db.structures.LanguageString;
import no.bibsys.db.structures.SimpleEntry;


public abstract class LocalDynamoTest extends DynamoTest {


    protected AmazonDynamoDB localClient;


    @Before
    public void init() {
        System.setProperty("sqlite4java.library.path", "build/libs");
        System.setProperty("java.library.path", "native-libs");
        System.setProperty("sqlite4java.library.path", "build/libs");

        localClient = DynamoDBEmbedded.create().amazonDynamoDB();

    }

    protected TableDriver newTableDriver() {
        return TableDriver.create(localClient);
    }

    protected ItemDriver newItemDriver(TableDriver tableDriver) {
        return ItemDriver.create(tableDriver);
    }


    protected SimpleEntry newSimpleEntry() {
        SimpleEntry entry = new SimpleEntry();
        entry.setId("ID1");
        List<LanguageString> languageStrings = new ArrayList<>();
        languageStrings.add(new LanguageString("the message", "en"));
        entry.setPreferredLabels(languageStrings);
        return entry;
    }

}
