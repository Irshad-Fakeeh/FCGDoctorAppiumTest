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

        // Click the profile image in the profile screen
        profilePage.clickProfileImage();

        System.out.println("Waiting for image dialog to open...");
        Thread.sleep(3000); // Wait for dialog

        // Close the profile image dialog
        profilePage.closeProfileImageDialog();

        System.out.println("Waiting after closing image dialog...");
        Thread.sleep(2000);

        // Click the QR code in the profile screen
        profilePage.clickQRCode();

        System.out.println("Waiting for QR dialog to open...");
        Thread.sleep(3000); // Wait for dialog

        // Close the QR dialog
        profilePage.closeQRDialog();

        System.out.println("Waiting after closing QR dialog...");
        Thread.sleep(2000);

        // Click the Privacy Policy in the profile screen
        profilePage.clickPrivacyPolicy();

        System.out.println("Clicked Privacy Policy. Pressing back to return to profile...");
        Thread.sleep(3000); // Wait for privacy policy to open

        // Press back to close privacy policy and return to profile
        ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));

        System.out.println("Waiting after back from privacy policy...");
        Thread.sleep(2000);

        // Click the Change Language in the profile screen
        profilePage.clickChangeLanguage();

        System.out.println("Opened language dialog. Selecting Arabic...");
        Thread.sleep(2000);

        // Select Arabic
        // try {
        //     profilePage.selectArabic();
        //     System.out.println("Selected Arabic. Closing dialog...");
        // } catch (Exception e) {
        //     System.out.println("Failed to select Arabic: " + e.getMessage());
        //     System.out.println("Closing dialog anyway...");
        // }

        // Close the language dialog
        profilePage.closeLanguageDialog();

        System.out.println("Closed dialog. Waiting...");
        Thread.sleep(2000);

        // Click Screen Mode
        profilePage.clickScreenMode();

        System.out.println("Opened screen mode dialog. Selecting Dark...");
        Thread.sleep(2000);

        // Select Dark
        profilePage.selectDarkTheme();

        System.out.println("Selected Dark. Selecting Device...");
        Thread.sleep(2000);

        profilePage.clickScreenMode();

        // Select Device
        profilePage.selectDeviceTheme();

        System.out.println("Selected Device. Closing dialog...");
        Thread.sleep(2000);

        // Close the screen mode dialog
        profilePage.closeScreenModeDialog();

        System.out.println("Closed screen mode dialog. Session remains open for further inspection.");
        // Keep the session open - remove driver.quit() to prevent closing

        
        Thread.sleep(5000);


    }
}