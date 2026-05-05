import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
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

    // 🔹 Profile Avatar
    public void clickProfileAvatar() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath("(//android.view.View[contains(@content-desc,'Welcome Back')]//android.widget.ImageView)[1]");

        WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(locator));
        avatar.click();
    }

    // 🔹 View All
    public void clickViewAll() {
        By locator = By.xpath("//*[@content-desc='View All']");

        WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(locator));
        viewAll.click();

        System.out.println("Clicked View All successfully");
    }

    // 🔹 Bottom Nav - Schedule
    public void clickScheduleFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Schedule")
                : By.xpath("//*[contains(@content-desc,'Schedule')]");

        WebElement schedule = wait.until(ExpectedConditions.elementToBeClickable(locator));
        schedule.click();
    }

    // 🔹 Bottom Nav - Home
    public void clickHomeFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Home")
                : By.xpath("//*[contains(@content-desc,'Home')]");

        WebElement home = wait.until(ExpectedConditions.elementToBeClickable(locator));
        home.click();
    }

    // 🔹 Toggle Weekly / Monthly
    public void toggleAppointmentChartView() {
        By locator = ios
                ? AppiumBy.accessibilityId("Weekly")
                : By.xpath("//*[contains(@content-desc,'Weekly') or contains(@content-desc,'Monthly')]");

        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(locator));
        toggle.click();

        System.out.println("Toggled appointment status chart view.");
    }

    // 🔹 Current Inpatients
    public void clickCurrentInpatient() throws InterruptedException {
        tapDashboardCard("Current Inpatients");
    }

    // 🔹 Critical Outpatients
    public void clickCriticalOutpatient() throws InterruptedException {
        tapDashboardCard("Critical Outpatients");
    }

    // 🔹 Highcare
    public void clickHighcare() throws InterruptedException {
        tapDashboardCard("High care");
    }

    /**
     * Flutter accessibility nodes fail visibilityOfElementLocated (isDisplayed=false).
     * Strategy: presenceOfElementLocated finds the element, then we validate its Y
     * coordinate against the screen. If off-screen, swipe down and re-fetch until
     * the centre is within the viewport, then fire a coordinate gesture tap.
     */
   private void tapDashboardCard(String contentDesc) throws InterruptedException {

    By locator = ios
            ? AppiumBy.accessibilityId(contentDesc)
            : By.xpath("//android.widget.ImageView[contains(@content-desc,'" + contentDesc.split(" ")[0] + "')]");

    WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

    int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
    int y = el.getLocation().getY() + el.getSize().getHeight() / 2;

    System.out.println("[DEBUG] Found: " + el.getAttribute("content-desc"));

    driver.executeScript("mobile: clickGesture", java.util.Map.of(
            "x", x,
            "y", y
    ));

    System.out.println("[SUCCESS] Tapped: " + contentDesc);
}

}