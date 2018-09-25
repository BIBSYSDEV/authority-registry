package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class IoUtils implements IoTestUtils {

    public String streamToString(final InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream,
            StandardCharsets.UTF_8));
        final StringBuilder output = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }


    public String readerToString(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuffer = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            stringBuffer.append(line);
            line = reader.readLine();
        }
        return stringBuffer.toString();
    }

}
