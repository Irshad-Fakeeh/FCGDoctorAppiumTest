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

    /**
     * Redirects to the current inpatient section from the dashboard.
     */
    public void clickCurrentInpatient() {
        if (ios) {
            tapElement(AppiumBy.accessibilityId("Current Inpatient"), "Current Inpatient");
            return;
        }
        scrollToTop();
        dumpPageSourceKeywords("inpatient", "patient");
        tapDashboardCard("inpatient", "Current Inpatient");
    }

    /**
     * Redirects to the critical outpatient section from the dashboard.
     */
    public void clickCriticalOutpatient() {
        if (ios) {
            tapElement(AppiumBy.accessibilityId("Critical Outpatient"), "Critical Outpatient");
            return;
        }
        dumpPageSourceKeywords("outpatient", "critical");
        tapDashboardCard("outpatient", "Critical Outpatient");
    }

    /**
     * Finds a dashboard card whose content-desc contains the given keyword
     * (case-insensitive) and taps it via coordinate gesture.
     */
    private void tapDashboardCard(String keyword, String label) {
        try {
            // Wait up to 15s for at least one matching element to appear
            By broad = By.xpath("//android.view.View[@content-desc]");
            wait.withTimeout(java.time.Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(broad));

            java.util.List<WebElement> allViews = driver.findElements(broad);
            System.out.println("[DIAG] Total android.view.View[@content-desc] elements: " + allViews.size());

            WebElement target = null;
            for (WebElement v : allViews) {
                String desc = v.getAttribute("content-desc");
                if (desc != null && desc.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println("[DIAG] Match → content-desc: " + desc);
                    target = v;
                    break;
                }
            }

            if (target == null) {
                // scroll down once and retry
                scrollDown();
                Thread.sleep(1500);
                allViews = driver.findElements(broad);
                for (WebElement v : allViews) {
                    String desc = v.getAttribute("content-desc");
                    if (desc != null && desc.toLowerCase().contains(keyword.toLowerCase())) {
                        System.out.println("[DIAG] Match after scroll → content-desc: " + desc);
                        target = v;
                        break;
                    }
                }
            }

            if (target == null) {
                throw new RuntimeException("No element found with keyword '" + keyword + "' in content-desc");
            }

            int x = target.getLocation().getX() + target.getSize().getWidth() / 2;
            int y = target.getLocation().getY() + target.getSize().getHeight() / 2;
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped dashboard card: " + label);

        } catch (Exception e) {
            System.out.println("[ERROR] tapDashboardCard failed for '" + label + "': " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void dumpPageSourceKeywords(String... keywords) {
        try {
            String src = driver.getPageSource();
            System.out.println("[DIAG] ===== PAGE SOURCE (keyword filter) =====");
            for (String line : src.split("\n")) {
                String lower = line.toLowerCase();
                for (String kw : keywords) {
                    if (lower.contains(kw.toLowerCase())) {
                        System.out.println("[DIAG] " + line.trim());
                        break;
                    }
                }
            }
            System.out.println("[DIAG] ==========================================");
        } catch (Exception e) {
            System.out.println("[DIAG] Could not dump page source: " + e.getMessage());
        }
    }

    private void scrollToTop() {
        if (ios) return;
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            driver.executeScript("mobile: swipeGesture", java.util.Map.of(
                    "left", width / 4,
                    "top", (int) (height * 0.2),
                    "width", width / 2,
                    "height", (int) (height * 0.6),
                    "direction", "down",
                    "percent", 0.9,
                    "speed", 600));
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("[WARNING] scrollToTop failed: " + e.getMessage());
        }
    }

    private void scrollDown() {
        if (ios) return;
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            driver.executeScript("mobile: swipeGesture", java.util.Map.of(
                    "left", width / 4,
                    "top", (int) (height * 0.2),
                    "width", width / 2,
                    "height", (int) (height * 0.6),
                    "direction", "up",
                    "percent", 0.6,
                    "speed", 600));
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("[WARNING] scrollDown failed: " + e.getMessage());
        }
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
