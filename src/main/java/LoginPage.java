import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
                    : By.id("com.android.permissioncontroller:id/permission_allow_button");

            WebElement allowButton = wait.until(
                    ExpectedConditions.elementToBeClickable(allowLocator)
            );
            allowButton.click();
        } catch (Exception ignored) {
            // Permission dialog not shown; continue
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
        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//android.widget.EditText)[1]")
                )
        );
        username.click();
        try {
            Thread.sleep(1000); // Wait for keyboard
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        username.clear();
        username.sendKeys(employeeId);

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//android.widget.EditText)[2]")
                )
        );
        password.click();
        try {
            Thread.sleep(1000); // Wait for keyboard
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        password.clear();
        password.sendKeys(passwordText);

        WebElement loginBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//android.view.View[@content-desc='Log In'])[2]")
                )
        );
        loginBtn.click();
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
}
