import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.utils.IoUtils;
import org.json.JSONObject;
import org.junit.Test;


public class IOUtilsTest {


  @Test
  public void IOUtilsShouldReadTextFromResources() throws IOException {
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
  public void IOUtilsShouldReadJsonFiles() throws IOException {
    Path path = Paths.get("json", "sample.json");
    IoUtils ioUtils = new IoUtils();
    String jsonString = ioUtils.resourceAsString(path);
    JSONObject json = new JSONObject(jsonString);
    assertThat(json.getInt("id"), is(equalTo(1)));
    assertThat(json.getString("label"), is(equalTo("TheLabel")));
    assertThat(json.getJSONObject("body").getString("message"), is(equalTo("Hello world")));
  }

}
