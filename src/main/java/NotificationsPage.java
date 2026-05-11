import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class NotificationsPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public NotificationsPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    public void testNotifications() throws InterruptedException {
        try {
            clickNotificationsFromBottomNav();
            Thread.sleep(3000);

            System.out.println("[INFO] Waiting for notifications page to load...");
            int count = getNotificationsCount();
            System.out.println("[INFO] Total notifications: " + count);

            if (count > 0) {
                clickNotificationByIndex(1);
                Thread.sleep(3000);
                goBackToNotificationsPage();
                Thread.sleep(1000);
            } else {
                System.out.println("[INFO] No notifications available to click.");
            }

            System.out.println("[INFO] Notifications test completed.");

            // Return to Home via bottom nav
            // goBackToHomePage();
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("[ERROR] Notifications test failed: " + e.getMessage());
            e.printStackTrace();
            // Best-effort return to home
            try {
                // goBackToHomePage();
            } catch (Exception ignored) {}
        }
    }

    public void clickNotificationsFromBottomNav() {
        try {
            By locator = ios
                    ? AppiumBy.accessibilityId("Notifications")
                    : By.xpath("//*[contains(@content-desc,'Notifications') or contains(@content-desc,'notifications')]");

            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
            int y = el.getLocation().getY() + el.getSize().getHeight() / 2;
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped Notifications bottom nav");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not tap Notifications nav: " + e.getMessage());
        }
    }

    public int getNotificationsCount() {
        try {
            By locator = By.xpath("//android.widget.ScrollView//android.view.View[@content-desc]");
            return driver.findElements(locator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickNotificationByIndex(int index) {
        try {
            By locator = By.xpath(
                    "(//android.widget.ScrollView//android.view.View[@content-desc])[" + index + "]");
            List<WebElement> found = driver.findElements(locator);
            if (found.isEmpty()) {
                System.out.println("[INFO] No notification at index " + index);
                return;
            }
            WebElement el = found.get(0);
            int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
            int y = el.getLocation().getY() + el.getSize().getHeight() / 2;
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped notification at index " + index);
        } catch (Exception e) {
            System.out.println("[WARNING] Could not tap notification: " + e.getMessage());
        }
    }

    public void goBackToNotificationsPage() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned to notifications list");
        } catch (Exception e) {
            System.out.println("[WARNING] Back press failed: " + e.getMessage());
        }
    }

    public void goBackToHomePage() {
        try {
            By locator = ios
                    ? AppiumBy.accessibilityId("Home")
                    : By.xpath("//*[contains(@content-desc,'Home')]");
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
            int y = el.getLocation().getY() + el.getSize().getHeight() / 2;
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Navigated to Home via bottom nav");
        } catch (Exception e) {
            System.out.println("[WARNING] Home navigation failed: " + e.getMessage());
        }
    }
}
