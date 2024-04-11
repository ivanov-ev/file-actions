package tests;

import com.codeborne.pdftest.PDF;

import com.fasterxml.jackson.core.*;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.codeborne.xlstest.XLS;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.xlstest.XLS.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileParsingTest extends TestBase {

    private ClassLoader classLoader = FileParsingTest.class.getClassLoader();

    @Test
    public void Test() throws Exception {
        zipFileParsingTest();
        XlsParsingTest();
        csvFileParsingTest();
        pdfFileParsingTest();
        jsonParsingTest();
    }

    private void XlsParsingTest() throws Exception {

        System.out.println("\nParsing XLSX...");

        XLS spreadsheet = new XLS(URI.create("file:///C:/temp/sample.xlsx"));

        System.out.println("Total number of spreadsheets: " + spreadsheet.excel.getNumberOfSheets());

        System.out.println("Spreadsheet names:");
        for (int i = 0; i < spreadsheet.excel.getNumberOfSheets(); i++) {
            System.out.println(spreadsheet.excel.getSheetName(i));
        }

        assertThat(spreadsheet, containsRow("Italia", "Milan"));
        assertThat(spreadsheet, containsRow("France", "Paris"));
        assertThat(spreadsheet, containsRow("Japan", "Tokyo"));

        assertThat(spreadsheet, containsRow("Repair the computer", "In Progress"));
        assertThat(spreadsheet, containsRow("Buy food", "Done"));
        assertThat(spreadsheet, containsRow("Pay bills", "Done"));
    }

    private void csvFileParsingTest() throws Exception {

        System.out.println("\nParsing CSV...");

        try (
                FileInputStream inputStream = new FileInputStream("C:\\temp\\sample.csv");
                CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))
        ) {

            List<String[]> data = csvReader.readAll();//No trim for whitespaces

            System.out.println("Total lines: " + csvReader.getLinesRead());

            //CSV Header
            Assertions.assertArrayEquals(new String[]{"FirstName", "LastName", "JobTitle", "OrgUnit"}, data.get(0));

            //CSV data
            Assertions.assertArrayEquals(new String[]{"John", "Doe", "Software Developer", "R&D Department"}, data.get(1));
            Assertions.assertArrayEquals(new String[]{"John", "Smith", "QA Engineer", "QA Department"}, data.get(2));
        }
    }

    private void pdfFileParsingTest() throws Exception {

        System.out.println("\nParsing PDF...");
        PDF pdf = new PDF(new File("C:/temp/sample.pdf"));

        System.out.println("PDF:\nTotal pages: " + pdf.numberOfPages + "; created with: " + pdf.producer);

        System.out.println("\nPDF content (plain text):");
        System.out.println(pdf.text);

        assertThat(pdf, PDF.containsText("Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas."));
        assertThat(pdf, PDF.containsText("Sed gravida venenatis ex non gravida."));
    }

    private void zipFileParsingTest() throws Exception {

        System.out.println("\nUnzipping...");

        try (ZipInputStream zipInputStream = new ZipInputStream(classLoader.getResourceAsStream("sample.zip"))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                System.out.println(entry.getName());

                String path = "C:/temp/";
                path = path + entry.getName();
                File file = new File(path);

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                for (int i = zipInputStream.read(); i != -1; i = zipInputStream.read()) {
                    fileOutputStream.write(i);
                }
                fileOutputStream.close();
            }
        }
    }

    void jsonParsingTest() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        System.out.println("JSON file content:\n" + json + "\n");

        JsonFactory jFactory = new JsonFactory();
        JsonParser jParser = jFactory.createParser(json);

        String parsedCompanyName;
        List<String> employees = new LinkedList<String>();

        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jParser.getCurrentName();

            if ("companyName".equals(fieldName)) {
                jParser.nextToken();
                parsedCompanyName = jParser.getText();
                System.out.println("Read companyName: " + parsedCompanyName);
                assertEquals(parsedCompanyName, "Best Company");
            }

            if ("employees".equals(fieldName)) {
                jParser.nextToken();
                while (jParser.nextToken() != JsonToken.END_ARRAY) {
                    employees.add(jParser.getText());
                }
                System.out.println("Read employees: " + employees);
                assertEquals(employees, Arrays.asList("John Doe", "Mike Smith", "Phil Jackson"));
            }
        }
        jParser.close();
    }
}