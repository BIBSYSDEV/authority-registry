package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


/**
 * Helper class for reading the output of methods that write to OutputStreams
 */
public class ReadableOutputStream {

  private final PipedInputStream inputStream;
  private final PipedOutputStream outputStream;
  private final BufferedReader reader;

  public ReadableOutputStream(final PipedInputStream inputStream,
      final PipedOutputStream outputStream,
      final BufferedReader reader) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.reader = reader;

  }


  public static ReadableOutputStream create() throws IOException {
    final PipedInputStream inputStream = new PipedInputStream();
    final PipedOutputStream outputStream = new PipedOutputStream(inputStream);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
    return new ReadableOutputStream(inputStream, outputStream, reader);
  }


  public PipedInputStream getInputStream() {
    return inputStream;
  }


  public PipedOutputStream getOutputStream() {
    return outputStream;
  }


  public BufferedReader getReader() {
    return reader;
  }


}
