package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils implements IOTestUtils {

  public String streamToString(InputStream stream) throws IOException {
    BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
    StringBuffer output=new StringBuffer();
    String line=reader.readLine();
    while(line!=null){
      output.append(line);
      line=reader.readLine();
    }
    return output.toString();
  }


  public String readerToString(BufferedReader reader) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    String line = reader.readLine();
    while (line != null) {
      stringBuffer.append(line);
      line = reader.readLine();
    }
    return stringBuffer.toString();
  }

}
