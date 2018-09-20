import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.utils.IoUtils;
import org.junit.Test;


public class IoUtilsTest {


    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void ioUtilsShouldReadTextFromResources() throws IOException {
        Path path = Paths.get("txt", "singleLineInput.txt");
        IoUtils ioUtils = new IoUtils();
        List<String> list = ioUtils.resouceAsList(path);
        assertThat(list.get(0), is(equalTo("Line1")));
        assertThat(list.get(1), is(equalTo("Line2")));
        assertThat(list.get(2), is(equalTo("Line3")));

        String everythingInOneLine = ioUtils.resourceAsString(path);
        assertThat(everythingInOneLine, is(equalTo("Line1 Line2 Line3")));
    }


    @Test
    public void ioUtilsShouldReadJsonFiles() throws IOException {
        Path path = Paths.get("json", "sample.json");
        IoUtils ioUtils = new IoUtils();
        String jsonString = ioUtils.resourceAsString(path);
        JsonNode json = mapper.readTree(jsonString);

        JsonNode node = json.get("body");

        assertThat(json.get("id").asInt(), is(equalTo(1)));
        assertThat(json.get("label").asText(), is(equalTo("TheLabel")));
        assertThat(json.get("body").get("message").asText(), is(equalTo("Hello world")));
    }

}
