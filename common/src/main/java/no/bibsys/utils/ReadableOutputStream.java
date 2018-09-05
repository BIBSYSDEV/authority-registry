package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


/**
 *
 * Helper class for reading the output of methods that write to OutputStreams
 *
 */
public class ReadableOutputStream {

  public final PipedInputStream inputStream;
  public final PipedOutputStream outputStream;
  public final BufferedReader reader;

  public ReadableOutputStream(PipedInputStream inputStream,
      PipedOutputStream outputStream,
      BufferedReader reader) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.reader = reader;

  }



  public static ReadableOutputStream create() throws IOException {
    PipedInputStream inputStream = new PipedInputStream();
    PipedOutputStream outputStream = new PipedOutputStream(inputStream);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    return new ReadableOutputStream(inputStream,outputStream,reader);
  }




}
