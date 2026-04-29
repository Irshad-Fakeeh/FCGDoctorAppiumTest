import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public HomePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Taps the doctor profile avatar in the AppBar to navigate to Profile &
     * Settings screen.
     * Flutter renders the GestureDetector wrapping the profile image at the
     * top-left of the AppBar.
     */
    public void clickProfileAvatar() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath(
                        "(//android.view.View[contains(@content-desc,'Welcome Back')]//android.widget.ImageView)[1]");

        WebElement avatar = wait.until(
                ExpectedConditions.elementToBeClickable(locator));
        avatar.click();
    }

    // view all appointments
    public void clickViewAll() {

        By locator = By.xpath("//*[@content-desc='View All']");

        WebElement viewAll = wait.until(
                ExpectedConditions.elementToBeClickable(locator));

        viewAll.click();

        System.out.println("Clicked View All successfully");
    }

    public void clickScheduleFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Schedule")
                : By.xpath("//*[contains(@content-desc,'Schedule')]");

        WebElement schedule = wait.until(ExpectedConditions.elementToBeClickable(locator));
        schedule.click();
    }

    public void clickHomeFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Home")
                : By.xpath("//*[contains(@content-desc,'Home')]");

        WebElement home = wait.until(ExpectedConditions.elementToBeClickable(locator));
        home.click();
    }

    /**
     * Toggles the appointment status chart between weekly and monthly views.
     */
    public void toggleAppointmentChartView() {
        By locator = ios
                ? AppiumBy.accessibilityId("Weekly") // Assuming accessibility ID for iOS
                : By.xpath("//*[contains(@content-desc,'Weekly') or contains(@content-desc,'Monthly')]");

        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(locator));
        toggle.click();
        System.out.println("Toggled appointment status chart view.");
    }

    public void clickCurrentInpatient() {
        By locator = ios
                ? AppiumBy.accessibilityId("Current Inpatient")
                : By.xpath("//*[contains(@content-desc,'Current Inpatient') or contains(@content-desc,'Inpatient')]");

        tapElement(locator, "Current Inpatient");
    }

    public void clickCriticalOutpatient() {
        By locator = ios
                ? AppiumBy.accessibilityId("Critical Outpatient")
                : By.xpath("//*[contains(@content-desc,'Critical Outpatient') or contains(@content-desc,'Critical Out')]");

        tapElement(locator, "Critical Outpatient");
    }

    private void tapElement(By locator, String name) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
            int y = el.getLocation().getY() + el.getSize().getHeight() / 2;
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped: " + name);
        } catch (Exception e) {
            System.out.println("[ERROR] Could not tap " + name + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
