package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.xlstest.XLS.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExtractZipAndParseFilesTest {

    private ClassLoader classLoader = ExtractZipAndParseFilesTest.class.getClassLoader();

    @DisplayName("Extract ZIP and parse CSV")
    @Test
    public void extractZipAndParseCsvFileTest() throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(classLoader.getResourceAsStream("sample.zip"))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains(".csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zipInputStream));
                    List<String[]> data = csvReader.readAll();//No trim for whitespaces

                    //CSV Header
                    Assertions.assertArrayEquals(new String[]{"FirstName", "LastName", "JobTitle", "OrgUnit"}, data.get(0));

                    //CSV data
                    Assertions.assertArrayEquals(new String[]{"John", "Doe", "Software Developer", "R&D Department"}, data.get(1));
                    Assertions.assertArrayEquals(new String[]{"John", "Smith", "QA Engineer", "QA Department"}, data.get(2));
                }
            }
        }
    }

    @DisplayName("Extract ZIP and parse XLSX")
    @Test
    public void extractZipAndParseXlsxFileTest() throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(classLoader.getResourceAsStream("sample.zip"))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains(".xlsx")) {
                    XLS spreadsheet = new XLS(zipInputStream);

                    assertThat(spreadsheet, containsRow("Italia", "Milan"));
                    assertThat(spreadsheet, containsRow("France", "Paris"));
                    assertThat(spreadsheet, containsRow("Japan", "Tokyo"));

                    assertThat(spreadsheet, containsRow("Repair the computer", "In Progress"));
                    assertThat(spreadsheet, containsRow("Buy food", "Done"));
                    assertThat(spreadsheet, containsRow("Pay bills", "Done"));
                }
            }
        }
    }

    @DisplayName("Extract ZIP and parse PDF")
    @Test
    public void extractZipAndParsePdfFileTest() throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(classLoader.getResourceAsStream("sample.zip"))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    PDF pdf = new PDF(zipInputStream);
                    assertThat(pdf, PDF.containsText("Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas."));
                    assertThat(pdf, PDF.containsText("Sed gravida venenatis ex non gravida."));
                }
            }
        }
    }
}
