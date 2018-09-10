package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils implements IOTestUtils {

  public String streamToString(final InputStream stream) throws IOException {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
    final StringBuffer output = new StringBuffer();
    String line = reader.readLine();
    while (line != null) {
      output.append(line);
      line = reader.readLine();
    }
    return output.toString();
  }


  public String readerToString(final BufferedReader reader) throws IOException {
    final StringBuffer stringBuffer = new StringBuffer();
    String line = reader.readLine();
    while (line != null) {
      stringBuffer.append(line);
      line = reader.readLine();
    }
    return stringBuffer.toString();
  }

}
