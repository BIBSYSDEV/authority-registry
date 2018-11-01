package no.bibsys.db;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Test;

public class TableDriverTest extends LocalDynamoTest{
 
    @Test
    public void test() {
        TableDriver driver = newTableDriver();
        try {
            driver.createTable("test");
            TableWriter writer = new TableWriter(driver, "test");
            Path path = Paths.get("json/sample.json");
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString()), StandardCharsets.UTF_8))) {
                List<String> jsonLines = reader.lines().collect(Collectors.toList());
                String json = String.join(" ", jsonLines);
                writer.addJson(json);
                writer.addJson(json.replace("id01", "id02"));
                writer.addJson(json.replace("id01", "id03"));
                writer.addJson(json.replace("id01", "id04"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            long tableSize = driver.tableSize("test");
            assertThat(tableSize, is(equalTo(4L)));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
