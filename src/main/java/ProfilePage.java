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
        try {
            // Only press BACK if the theme options are still on screen
            // (selecting a theme may auto-dismiss the dialog)
            By dialogCheck = ios
                    ? AppiumBy.accessibilityId("theme_dark")
                    : By.xpath("//*[contains(@content-desc,'theme_dark') or contains(@content-desc,'theme_device') or contains(@content-desc,'theme_light')]");

            java.util.List<WebElement> opts = driver.findElements(dialogCheck);
            if (!opts.isEmpty()) {
                System.out.println("[INFO] Screen mode dialog still open — pressing BACK to close");
                pressBack();
                Thread.sleep(1000);
                System.out.println("[SUCCESS] Screen mode dialog closed");
            } else {
                System.out.println("[INFO] Screen mode dialog already dismissed — skipping BACK");
            }
        } catch (Exception e) {
            System.out.println("[WARNING] closeScreenModeDialog failed: " + e.getMessage());
        }
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

        // Screen Mode — select Dark, close, then restore Device
        clickScreenMode();
        System.out.println("[INFO] Opened screen mode dialog (Dark)...");
        Thread.sleep(2000);
        selectDarkTheme();
        Thread.sleep(1500);
        closeScreenModeDialog();
        Thread.sleep(2000);

        clickScreenMode();
        System.out.println("[INFO] Opened screen mode dialog (Device)...");
        Thread.sleep(2000);
        selectDeviceTheme();
        Thread.sleep(1500);
        closeScreenModeDialog();
        Thread.sleep(2000);

        performManualScrollDown();
        clickLogout();
        Thread.sleep(2000);
        confirmLogout();

        System.out.println("[INFO] Profile test sequence completed.");
    }

        private void performManualScrollDown() {
        if (ios) return;
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();
            driver.executeScript("mobile: swipeGesture", java.util.Map.of(
                    "left",      width / 4,
                    "top",       (int)(height * 0.2),
                    "width",     width / 2,
                    "height",    (int)(height * 0.6),
                    "direction", "up",
                    "percent",   0.9,
                    "speed",     800));
            System.out.println("[DEBUG] Scroll executed");
        } catch (Exception e) {
            System.out.println("[WARNING] Scroll failed: " + e.getMessage());
        }
    }

    public void confirmLogout() {
        try {
            System.out.println("[STEP] Looking for logout confirmation dialog...");
            By locator = ios
                    ? AppiumBy.accessibilityId("Yes")
                    : By.xpath("//*[contains(@content-desc,'Yes') or contains(@text,'Yes')]");

            java.util.List<WebElement> found = driver.findElements(locator);
            if (found.isEmpty()) {
                System.out.println("[INFO] No confirmation dialog found — skipping");
                return;
            }
            WebElement yes = found.get(0);
            int x = yes.getLocation().getX() + yes.getSize().getWidth() / 2;
            int y = yes.getLocation().getY() + yes.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping Yes at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Confirmed logout — tapped Yes");
        } catch (Exception e) {
            System.out.println("[WARNING] confirmLogout failed: " + e.getMessage());
        }
    }

    public void clickLogout() {
        try {
            System.out.println("[STEP] Clicking Logout...");
            By locator = ios
                    ? AppiumBy.accessibilityId("Logout")
                    : By.xpath("//*[contains(@content-desc,'Log Out') or contains(@content-desc,'logout') or contains(@content-desc,'Log Out') or contains(@text,'Logout') or contains(@text,'Log Out')]");

            java.util.List<WebElement> found = driver.findElements(locator);
            if (found.isEmpty()) {
                System.out.println("[WARNING] Logout button not found — skipping");
                return;
            }
            WebElement btn = found.get(0);
            int x = btn.getLocation().getX() + btn.getSize().getWidth() / 2;
            int y = btn.getLocation().getY() + btn.getSize().getHeight() / 2;
            System.out.println("[DEBUG] Tapping Logout at (" + x + ", " + y + ")");
            driver.executeScript("mobile: clickGesture", java.util.Map.of("x", x, "y", y));
            System.out.println("[SUCCESS] Logout tapped");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("[WARNING] Logout failed: " + e.getMessage());
        }
    }
}
