import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) throws Exception {
        String platform = args.length > 0 ? args[0].trim().toLowerCase() : "android";

        // Create the platform-specific Appium session
        AppiumDriver driver = AppiumDriverFactory.createDriver(platform);

        // Shared wait helper used by page objects
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Page object encapsulates the login-screen actions
        LoginPage loginPage = new LoginPage(driver, wait, "ios".equals(platform));

        // Handle permission prompt and complete login
        loginPage.handlePermissionDialogIfPresent();
        loginPage.login("00001879", "1");

        // Give the app a moment to navigate after tapping "Log In"
        Thread.sleep(4000);

        // Clean up the Appium session
        driver.quit();
    }
}