package no.bibsys.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IoUtils {

    public static InputStream resourceAsStream(Path path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString());

    }

    public static List<String> resouceAsList(Path path) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(resourceAsStream(path),
            StandardCharsets.UTF_8)) {
            try (BufferedReader reader = new BufferedReader(isr)) {
                List<String> result = new ArrayList<>();
                String line = reader.readLine();
                while (line != null) {
                    result.add(line);
                    line = reader.readLine();
                }

                return result;
            }
        }
    }

    public static String resourceAsString(Path path) throws IOException {
        List<String> lines = resouceAsList(path);
        return String.join(" ", lines);
    }


    public static String streamToString(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            Optional<String> resultOpt = reader.lines()
                .reduce((s1, s2) -> String.format("%s%n%s", s1, s2));
            return resultOpt
                .orElseThrow(() -> new IOException("Error converting InputStream to String"));

        }
    }


}
