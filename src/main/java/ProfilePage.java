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

    /**
     * Clicks the profile image in the profile screen to open the image dialog.
     */
    public void clickProfileImage() {
        By locator = ios
                ? AppiumBy.accessibilityId("profile_image")
                : By.xpath("//*[@content-desc='profile_image']");

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
                : By.xpath("//*[@content-desc='qr_code']");

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
     * Clicks the Privacy Policy item in the profile screen.
     */
   public void clickPrivacyPolicy() {

    By locator = By.xpath(
        "//*[contains(@content-desc,'privacy_policy') or contains(@content-desc,'Privacy')]"
    );

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
}