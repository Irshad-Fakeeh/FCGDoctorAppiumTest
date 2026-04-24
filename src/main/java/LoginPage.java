import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.util.Collections;

public class LoginPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public LoginPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void handlePermissionDialogIfPresent() {
        try {
            By allowLocator = ios
                    ? AppiumBy.accessibilityId("Allow")
                    // Catch broader "Allow" buttons using native text matching
                    : AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)Allow|While using the app.*\")");

            WebElement allowButton = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(allowLocator));
            allowButton.click();
            System.out.println("Permission granted.");
        } catch (Exception ignored) {
            System.out.println("No permission dialog detected.");
        }
    }

    public void login(String employeeId, String passwordText) {
        if (ios) {
            fillIosLogin(employeeId, passwordText);
        } else {
            fillAndroidLogin(employeeId, passwordText);
        }
    }

    private void fillAndroidLogin(String employeeId, String passwordText) {
        System.out.println("Wait for app to load (Dynamic wait)...");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // Reduced from 10s to 2s to fix time delay
        
        System.out.println("Checking app progress...");
        String currentAct = "unknown";
        try {
            if (driver instanceof io.appium.java_client.android.AndroidDriver) {
                currentAct = ((io.appium.java_client.android.AndroidDriver) driver).currentActivity();
                System.out.println("CURRENT ACTIVITY: " + currentAct);
            }
        } catch (Exception ignored) {}
        
        if (currentAct.contains("LauncherActivity")) {
            System.out.println("App appears stuck on splash screen. Executing kickstart swipe...");
            performSplashKickstartSwipe();
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        }

        System.out.println("Scanning for Login screen elements...");
        
        // Strategy: First try standard EditText, fallback to anything with 'Employee' or 'ID' in it
        By userLocator = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)");
        By userBackup = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Employee\").instance(0)");
        By passLocator = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)");
        
        System.out.println("Entering Employee ID...");
        WebElement username = null;
        try {
            // Dynamic wait for stabilization
            username = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(userLocator));
        } catch (Exception e1) {
            System.out.println("Primary search (EditText) failed. Trying backup locator...");
            try {
                username = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(userBackup));
            } catch (Exception e2) {
                System.err.println("FAILED to find login fields. Performing emergency wakeup...");
                performSimpleWakeupTap();
                throw e2;
            }
        }
        
        username.click();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        username.clear();
        username.sendKeys(employeeId);

        System.out.println("Entering Password...");
        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(passLocator));
        password.click();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        password.clear();
        password.sendKeys(passwordText);

        System.out.println("Clicking Log In button...");
        // Use Button class instead of instance(1) to be more specific and stable
        By loginBtnLocator = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").descriptionContains(\"Log In\")");
        
        try {
            if (driver instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) driver).hideKeyboard();
                System.out.println("Keyboard hidden. Waiting for UI to settle...");
                Thread.sleep(3000); // Increased wait time to prevent UI resize crash
            }
        } catch (Exception ignored) {}

        WebElement loginBtn = null;
        try {
            // First attempt: look for a clickable element explicitly described as "Log In".
            // Adding .clickable(true) avoids catching static title texts and bypasses the need for the crash-prone .instance(1)
            By robustLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*Log In.*\").clickable(true)");
            loginBtn = wait.until(ExpectedConditions.elementToBeClickable(robustLocator));
        } catch (Exception e) {
            System.out.println("First locator failed, trying simple accessibilityId fallback...");
            loginBtn = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Log In")));
        }
        loginBtn.click();
        System.out.println("Login button clicked! Waiting for Home screen...");
    }

    private void fillIosLogin(String employeeId, String passwordText) {
        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        AppiumBy.accessibilityId("Employee ID")
                )
        );
        username.click();
        username.clear();
        username.sendKeys(employeeId);

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath("//XCUIElementTypeSecureTextField | //XCUIElementTypeTextField[not(@name='Employee ID')]")
                )
        );
        password.click();
        password.clear();
        password.sendKeys(passwordText);

        WebElement loginBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        AppiumBy.accessibilityId("Log In")
                )
        );
        loginBtn.click();
    }

    private void performSplashKickstartSwipe() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), width / 2, (int)(height * 0.8)));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), width / 2, (int)(height * 0.2)));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(swipe));
        } catch (Exception ignored) {}
    }

    private void performSimpleWakeupTap() {
        try {
            System.out.println("Executing Wake-up Tap: Tapping center of screen to refresh UI tree...");
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), width / 2, height / 2));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(tap));
            Thread.sleep(2000);
        } catch (Exception ignored) {}
    }
}
