import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileWriter;
import java.io.IOException;

public class ProfilePage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public ProfilePage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }



    /**
     * Clicks the profile image in the profile screen to open the image dialog.
     */
    public void clickProfileImage() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath("(//android.widget.ScrollView//android.widget.ImageView)[1]");

        WebElement image = wait.until(ExpectedConditions.elementToBeClickable(locator));
        image.click();
    }

    /**
     * Closes the profile image dialog by pressing the back key (taps outside or dismisses dialog).
     */
    public void closeProfileImageDialog() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            // For iOS, perhaps press back or find another way
            // Assuming back key works or add iOS specific
            driver.navigate().back();
        }
    }

    /**
     * Clicks the QR code in the profile screen.
     */
    public void clickQRCode() {
        By locator = ios
                ? AppiumBy.accessibilityId("qr_code")
                : By.xpath("(//android.widget.ScrollView//android.widget.ImageView)[2]");

        WebElement qrCode = wait.until(ExpectedConditions.elementToBeClickable(locator));
        qrCode.click();
    }

    /**
     * Closes the QR code dialog by pressing the back key.
     */
    public void closeQRDialog() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
    }

    /**
     * Clicks the Change Language item in the profile screen to toggle between English and Arabic.
     */
    public void clickChangeLanguage() {
        By locator = ios
                ? AppiumBy.accessibilityId("change_language")
                : By.xpath("//*[contains(@content-desc,'change_language') or contains(@content-desc,'Change Language')]");
        WebElement changeLanguage = wait.until(ExpectedConditions.elementToBeClickable(locator));
        changeLanguage.click();
    }

    /**
     * Closes the language dialog by pressing the back key.
     */
    public void closeLanguageDialog() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
    }

    /**
     * Selects Arabic from the language dialog.
     */
    public void selectArabic() {
        By locator = ios
                ? AppiumBy.accessibilityId("arabic")
                : By.xpath("//*[contains(@content-desc,'العربية')]");

        try {
            WebElement arabic = wait.until(ExpectedConditions.elementToBeClickable(locator));
            arabic.click();
        } catch (Exception e) {
            System.out.println("Failed to find Arabic element. Saving page source to page_source.xml");
            try (FileWriter fw = new FileWriter("page_source.xml")) {
                fw.write(driver.getPageSource());
            } catch (IOException io) {
                System.out.println("Failed to save page source: " + io.getMessage());
            }
            throw e;
        }
    }

    /**
     * Selects English from the language dialog.
     */
    public void selectEnglish() {
        By locator = ios
                ? AppiumBy.accessibilityId("english")
                : By.xpath("//*[contains(@content-desc,'English')]");

        WebElement english = wait.until(ExpectedConditions.elementToBeClickable(locator));
        english.click();
    }

    /**
     * Clicks the Screen Mode item in the profile screen.
     */
    public void clickScreenMode() {
        By locator = ios
                ? AppiumBy.accessibilityId("screen_mode")
                : By.xpath("//*[contains(@content-desc,'screen_mode') or contains(@content-desc,'Screen Mode')]");

        WebElement screenMode = wait.until(ExpectedConditions.elementToBeClickable(locator));
        screenMode.click();
    }

    /**
     * Selects Dark theme from the screen mode dialog.
     */
    public void selectDarkTheme() {
        By locator = ios
                ? AppiumBy.accessibilityId("theme_dark")
                : By.xpath("//*[contains(@content-desc,'theme_dark') or contains(@content-desc,'Dark')]");

        WebElement dark = wait.until(ExpectedConditions.elementToBeClickable(locator));
        dark.click();
    }

    /**
     * Selects Device theme from the screen mode dialog.
     */
    public void selectDeviceTheme() {
        By locator = ios
                ? AppiumBy.accessibilityId("theme_device")
                : By.xpath("//*[contains(@content-desc,'theme_device') or contains(@content-desc,'Device')]");
        WebElement device = wait.until(ExpectedConditions.elementToBeClickable(locator));
        device.click();
    }

    /**
     * Closes the screen mode dialog by pressing the back key.
     */
    public void closeScreenModeDialog() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
    }

    /**
     * Clicks the Privacy Policy item in the profile screen.
     */
    public void clickPrivacyPolicy() {
        By locator = ios
                ? AppiumBy.accessibilityId("privacy_policy")
                : By.xpath("//*[contains(@content-desc,'privacy_policy') or contains(@content-desc,'Privacy')]");

        WebElement privacyPolicy = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        privacyPolicy.click();
    }

 public void closePrivacyPolicy() {
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            // For iOS, perhaps press back or find another way
            // Assuming back key works or add iOS specific
            driver.navigate().back();
        }
    }

    /**
     * Verifies the Profile & Settings screen is visible.
     */
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

    /**
     * Performs the full profile test sequence: image, QR, privacy, language, screen mode.
     * After completion, redirects to Notifications from the bottom navigation.
     */
    public void performFullProfileTest() throws InterruptedException {

        // Click the profile image in the profile screen
        clickProfileImage();

        System.out.println("Waiting for image dialog to open...");
        Thread.sleep(3000); // Wait for dialog

        // Close the profile image dialog
        closeProfileImageDialog();

        System.out.println("Waiting after closing image dialog...");
        Thread.sleep(2000);

        // Click the QR code in the profile screen
        clickQRCode();

        System.out.println("Waiting for QR dialog to open...");
        Thread.sleep(3000); // Wait for dialog

        // Close the QR dialog
        closeQRDialog();

        System.out.println("Waiting after closing QR dialog...");
        Thread.sleep(2000);

        // Click the Privacy Policy in the profile screen
        clickPrivacyPolicy();

        System.out.println("Clicked Privacy Policy. Pressing back to return to profile...");
        Thread.sleep(3000); // Wait for privacy policy to open

        // Press back to close privacy policy and return to profile
        if (!ios) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }

        System.out.println("Waiting after back from privacy policy...");
        Thread.sleep(2000);

        // Click the Change Language in the profile screen
        clickChangeLanguage();

        System.out.println("Opened language dialog. Selecting Arabic...");
        Thread.sleep(2000);

        // Close the language dialog
        closeLanguageDialog();

        System.out.println("Closed dialog. Waiting...");
        Thread.sleep(2000);

        // Click Screen Mode
        clickScreenMode();

        System.out.println("Opened screen mode dialog. Selecting Dark...");
        Thread.sleep(2000);

        // Select Dark
        selectDarkTheme();

        System.out.println("Selected Dark. Selecting Device...");
        Thread.sleep(2000);

        clickScreenMode();

        // Select Device
        selectDeviceTheme();

        System.out.println("Selected Device. Closing dialog...");
        Thread.sleep(2000);

        // Close the screen mode dialog
        closeScreenModeDialog();

        System.out.println("Profile test sequence completed. Session remains open for further inspection.");
        // Keep the session open - remove driver.quit() to prevent closing

    }
}