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
     * Taps the doctor profile avatar in the AppBar to navigate to Profile & Settings screen.
     * Flutter renders the GestureDetector wrapping the profile image at the top-left of the AppBar.
     */
    public void clickProfileAvatar() {
        By locator = By.xpath(
    "(//*[contains(@content-desc,'Welcome Back')]//android.widget.ImageView)[1]"
);

    WebElement avatar = wait.until(
        ExpectedConditions.elementToBeClickable(locator)
    );
    avatar.click();
    }

    //view all appointments
    public void clickViewAll() {

    By locator = By.xpath("//*[@content-desc='View All']");

    WebElement viewAll = wait.until(
        ExpectedConditions.elementToBeClickable(locator)
    );

        viewAll.click();

        System.out.println("Clicked View All successfully");
    }
   }

