package tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    @BeforeAll
    static void setup() {
        //pay attention to the trailing slash, because it concatenates with open() and may result in 2 slashes
        Configuration.baseUrl = "https://demoqa.com";
        Configuration.pageLoadStrategy = "eager";
        //Configuration.browser = "Chrome";
        Configuration.browserSize = "1600x1000";
        Configuration.timeout = 5000; //5 sec; default is 4 sec

    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}
