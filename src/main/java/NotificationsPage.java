import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NotificationsPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public NotificationsPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    //test notifications
  public void testNotifications() throws InterruptedException {

        clickNotificationsFromBottomNav();
        Thread.sleep(3000);

        System.out.println("Waiting for notifications page to load...");
        int count = getNotificationsCount();
        System.out.println("Total notifications: " + count);

        if (count > 0) {
            System.out.println("Waiting for notifications page to load...");
            clickNotificationByIndex(1);
            System.out.println("Going back to home page...");
            Thread.sleep(3000);
            goBackToNotificationsPage();
            Thread.sleep(1000);
            goBackToHomePage();
        }
        else {
                System.out.println("No notifications available to click.");
        }

        System.out.println("Notifications test sequence completed. Navigating to Notifications...");
    }

    /**
     * Clicks the Notifications button in the bottom navigation to navigate to the Notifications screen.
     */
    public void clickNotificationsFromBottomNav() {
        By locator = ios
                ? AppiumBy.accessibilityId("Notifications")
                : By.xpath("//*[contains(@content-desc,'Notifications') or contains(@content-desc,'notifications')]");

        WebElement notifications = wait.until(ExpectedConditions.elementToBeClickable(locator));
        notifications.click();
    }


    /**
     * Gets the list of notifications on the screen.
     */
    public int getNotificationsCount() {
         By locator = By.xpath(
        "//android.widget.ScrollView//android.view.View[@content-desc]"
        );

        try {
            return driver.findElements(locator).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Checks if the notifications list is empty.
     */
    public boolean isNotificationsListEmpty() {
        By emptyLocator = ios
                ? AppiumBy.accessibilityId("empty_notifications")
                : By.xpath("//*[contains(@text,'No notifications') or contains(@content-desc,'empty')]");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emptyLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks on the first notification in the list.
     */
    public void clickFirstNotification() {
        By locator = ios
                ? AppiumBy.accessibilityId("notification_item")
                : By.xpath("(//*[contains(@content-desc,'notification')])[1]");

        WebElement notification = wait.until(ExpectedConditions.elementToBeClickable(locator));
        notification.click();
    }

    /**
     * Clicks on a specific notification by index (0-based).
     */
public void clickNotificationByIndex(int index) {

    By locator = By.xpath(
        "(//android.widget.ScrollView//android.view.View[@content-desc])[" + index + "]"
    );

    WebElement notification = wait.until(
        ExpectedConditions.elementToBeClickable(locator)
    );
    notification.click();

    System.out.println("Clicked notification at index: " + index);
}

    /**
     * Goes back to the home page by pressing the back button.
     */
    public void goBackToNotificationsPage() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
    }

    public void goBackToHomePage() {

    By locator;

    if (ios) {
        locator = AppiumBy.accessibilityId("Home");
    } else {
        locator = By.xpath("//*[contains(@content-desc,'Home')]");
    }

    WebElement homeTab = wait.until(
        ExpectedConditions.elementToBeClickable(locator)
    );

    homeTab.click();

    System.out.println("Navigated to Home via bottom nav");
}
}
