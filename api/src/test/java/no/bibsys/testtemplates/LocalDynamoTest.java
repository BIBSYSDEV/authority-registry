package no.bibsys.testtemplates;

import org.junit.Before;
import no.bibsys.LocalDynamoDBHelper;
import no.bibsys.db.DatabaseManager;

public abstract class LocalDynamoTest {

    public DatabaseManager databaseManager;
    public SampleData sampleData;

    @Before
    public void setUp() {
        System.setProperty("sqlite4java.library.path", "build/libs");
        databaseManager = new DatabaseManager(LocalDynamoDBHelper.getTableDriver());
        sampleData = new SampleData();
    }


}
