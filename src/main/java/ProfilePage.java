import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfilePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public ProfilePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    // Flutter-safe tap: presenceOfElementLocated + coordinate gesture
    private boolean flutterTap(By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            int x = el.getLocation().getX() + el.getSize().getWidth() / 2;
            int y = el.getLocation().getY() + el.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping " + label + " at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Tapped " + label);
            return true;
        } catch (Exception e) {
            System.out.println("[WARNING] Could not tap " + label + ": " + e.getMessage());
            return false;
        }
    }

    private void pressBack() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Back press failed: " + e.getMessage());
        }
    }

    public void clickProfileImage() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath("(//android.widget.ScrollView//android.widget.ImageView)[1]");
        flutterTap(locator, "Profile Image");
    }

    public void closeProfileImageDialog() {
        pressBack();
    }

    public void clickQRCode() {
        By locator = ios
                ? AppiumBy.accessibilityId("qr_code")
                : By.xpath("(//android.widget.ScrollView//android.widget.ImageView)[2]");
        flutterTap(locator, "QR Code");
    }

    public void closeQRDialog() {
        pressBack();
        System.out.println("[SUCCESS] Closed QR dialog");
    }

    public void clickChangeLanguage() {
        By locator = ios
                ? AppiumBy.accessibilityId("change_language")
                : By.xpath("//*[contains(@content-desc,'change_language') or contains(@content-desc,'Change Language')]");
        flutterTap(locator, "Change Language");
    }

    public void closeLanguageDialog() {
        pressBack();
    }

    public void selectArabic() {
        By locator = ios
                ? AppiumBy.accessibilityId("arabic")
                : By.xpath("//*[contains(@content-desc,'العربية')]");
        flutterTap(locator, "Arabic");
    }

    public void selectEnglish() {
        By locator = ios
                ? AppiumBy.accessibilityId("english")
                : By.xpath("//*[contains(@content-desc,'English')]");
        flutterTap(locator, "English");
    }

    public void clickScreenMode() {
        By locator = ios
                ? AppiumBy.accessibilityId("screen_mode")
                : By.xpath("//*[contains(@content-desc,'screen_mode') or contains(@content-desc,'Screen Mode')]");
        flutterTap(locator, "Screen Mode");
    }

    public void selectDarkTheme() {
        By locator = ios
                ? AppiumBy.accessibilityId("theme_dark")
                : By.xpath("//*[contains(@content-desc,'theme_dark') or contains(@content-desc,'Dark')]");
        flutterTap(locator, "Dark Theme");
    }

    public void selectDeviceTheme() {
        By locator = ios
                ? AppiumBy.accessibilityId("theme_device")
                : By.xpath("//*[contains(@content-desc,'theme_device') or contains(@content-desc,'Device')]");
        flutterTap(locator, "Device Theme");
    }

    public void closeScreenModeDialog() {
        pressBack();
    }

    public void clickPrivacyPolicy() {
        By locator = ios
                ? AppiumBy.accessibilityId("privacy_policy")
                : By.xpath("//*[contains(@content-desc,'privacy_policy') or contains(@content-desc,'Privacy')]");
        flutterTap(locator, "Privacy Policy");
    }

    public void closePrivacyPolicy() {
        pressBack();
        System.out.println("[SUCCESS] Closed Privacy Policy");
    }

    public boolean isProfileScreenVisible() {
        By locator = ios
                ? By.xpath("//*[@name='Profile & Settings']")
                : By.xpath("//*[@text='Profile & Settings']");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void performFullProfileTest() throws InterruptedException {

        clickProfileImage();
        System.out.println("[INFO] Waiting for image dialog...");
        Thread.sleep(3000);
        closeProfileImageDialog();
        Thread.sleep(2000);

        clickQRCode();
        System.out.println("[INFO] Waiting for QR dialog...");
        Thread.sleep(3000);
        closeQRDialog();
        Thread.sleep(4000);

        clickPrivacyPolicy();
        System.out.println("[INFO] Waiting for Privacy Policy...");
        Thread.sleep(3000);
        closePrivacyPolicy();
        Thread.sleep(2000);

        clickChangeLanguage();
        System.out.println("[INFO] Opened language dialog...");
        Thread.sleep(2000);
        closeLanguageDialog();
        Thread.sleep(2000);

        clickScreenMode();
        System.out.println("[INFO] Opened screen mode dialog...");
        Thread.sleep(2000);
        selectDarkTheme();
        Thread.sleep(2000);

        clickScreenMode();
        Thread.sleep(2000);
        selectDeviceTheme();
        Thread.sleep(2000);
        closeScreenModeDialog();

        System.out.println("[INFO] Profile test sequence completed.");
    }
}
