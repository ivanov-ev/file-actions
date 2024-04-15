package tests;

import com.fasterxml.jackson.core.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseJsonFileTest {

    @DisplayName("Parse JSON")
    @Test
    void parseJsonTest() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));

        JsonFactory jFactory = new JsonFactory();
        JsonParser jParser = jFactory.createParser(json);

        String parsedCompanyName;
        List<String> employees = new LinkedList<String>();

        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jParser.getCurrentName();

            if ("companyName".equals(fieldName)) {
                jParser.nextToken();
                parsedCompanyName = jParser.getText();
                assertEquals(parsedCompanyName, "Best Company");
            }

            if ("employees".equals(fieldName)) {
                jParser.nextToken();
                while (jParser.nextToken() != JsonToken.END_ARRAY) {
                    employees.add(jParser.getText());
                }
                assertEquals(employees, Arrays.asList("John Doe", "Mike Smith", "Phil Jackson"));
            }
        }
        jParser.close();
    }
}
