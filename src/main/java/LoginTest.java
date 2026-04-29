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
        // homePage.clickProfileAvatar();

        // System.out.println("Waiting for profile page to load...");
        // Thread.sleep(5000); // Wait for navigation

        // // Perform the full profile test sequence
        // ProfilePage profilePage = new ProfilePage(driver, wait,
        // "ios".equals(platform));
        // profilePage.performFullProfileTest();
        // Thread.sleep(2000);

        System.out.println("Toggling appointment status chart to monthly...");
        homePage.toggleAppointmentChartView();
        Thread.sleep(2000);

        // System.out.println("Toggling appointment status chart back to weekly...");
        // homePage.toggleAppointmentChartView();
        // Thread.sleep(2000);

        System.out.println("Navigating to Current Inpatient section...");
        homePage.clickCurrentInpatient();
        Thread.sleep(3000);

        CurrentInPatientPage inpatientPage = new CurrentInPatientPage(driver, wait, "ios".equals(platform));
        inpatientPage.TestInPatient();

        System.out.println("Navigating to Critical Outpatient section...");
        homePage.clickCriticalOutpatient();
        Thread.sleep(3000);

        CriticalOutPatientPage outpatientPage = new CriticalOutPatientPage(driver, wait, "ios".equals(platform));
        outpatientPage.testCriticalOutpatient();

        // Now navigate to notifications page - test notifications
        NotificationsPage notificationsPage = new NotificationsPage(driver, wait, "ios".equals(platform));
        notificationsPage.testNotifications();

        System.out.println("Navigating to View All appointments...");
        Thread.sleep(2000);
        homePage.clickScheduleFromBottomNav();
        Thread.sleep(3000);

        SchedulePage schedulePage = new SchedulePage(driver, wait, "ios".equals(platform));
        schedulePage.testSchedulePage();

        System.out.println("Navigating back to Home page...");
        homePage.clickHomeFromBottomNav();
        Thread.sleep(3000);

        System.out.println("All test sequences are completed. Session remains open for further inspection.");
        Thread.sleep(5000);

    }
}