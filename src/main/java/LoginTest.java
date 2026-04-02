import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Set;

import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) throws Exception {
        String platform = args.length > 0 ? args[0].trim().toLowerCase() : "android";

        System.out.println("Connecting to Appium server...");
        // Create the platform-specific Appium session
        AppiumDriver driver = AppiumDriverFactory.createDriver(platform);

        System.out.println("Session started successfully! Performing login actions...");
        // Shared wait helper used by page objects
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Page object encapsulates the login-screen actions
        LoginPage loginPage = new LoginPage(driver, wait, "ios".equals(platform));

        // Handle permission prompt and complete login
        loginPage.handlePermissionDialogIfPresent();
        loginPage.login("00001879", "1");

        System.out.println("Waiting for app navigation...");
        // Give the app a moment to navigate after tapping "Log In"
        Thread.sleep(15000);

        // Now on homepage, click notifications
        HomePage homePage = new HomePage(driver, wait, "ios".equals(platform));
        homePage.clickProfileAvatar();

        System.out.println("Waiting for profile page to load...");
        Thread.sleep(5000); // Wait for navigation

        // Now on profile page
        ProfilePage profilePage = new ProfilePage(driver, wait, "ios".equals(platform));

        // Perform the full profile test sequence
        profilePage.performFullProfileTest();

        System.out.println("Profile test sequence completed. Session remains open for further inspection.");
        // Keep the session open - remove driver.quit() to prevent closing

        
        Thread.sleep(5000);


    }
}