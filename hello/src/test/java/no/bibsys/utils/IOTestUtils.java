package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface IOTestUtils {


  default InputStream resourceAsStream(Path path){
    return this.getClass().getClassLoader().getResourceAsStream(path.toString());

  }


  default List<String> resouceAsList(Path path) throws IOException {
    BufferedReader reader=new BufferedReader(new InputStreamReader(resourceAsStream(path)));
    ArrayList<String> result=new ArrayList<>();
    String line=reader.readLine();
    while(line!=null){
      result.add(line);
      line=reader.readLine();
    }

    return result;

  }


  default String resourceAsString(Path path) throws IOException {
    List<String> lines=resouceAsList(path);
    String result=String.join(" ",lines);
    return result;
  }


}
