
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.util.Collections;
import java.time.Duration;
import java.util.List;

public class CurrentInpatientsPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public CurrentInpatientsPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Checks if the list of current inpatients is empty.
     */
    public boolean isListEmpty() {
        By emptyStateLocator = AppiumBy.accessibilityId("No records found"); // Common pattern in this app
        
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(emptyStateLocator));
            return true;
        } catch (Exception e) {
            // Check if at least one card is present
            try {
                return driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").clickable(true)")).isEmpty();
            } catch (Exception ignored) {
                return true;
            }
        }
    }

    /**
     * Clicks on the patient card at the specified index.
     */
    public boolean clickPatientCard(int index) {
        // Use a more specific locator that looks for MRN in the description
        By cardLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*MRN.*\").instance(" + index + ")");
        try {
            WebElement card = wait.until(ExpectedConditions.elementToBeClickable(cardLocator));
            System.out.println("Tapping patient card index " + index + "...");
            tapElement(card);
            return true;
        } catch (Exception e) {
            System.err.println("Could not find patient card at index " + index + ". Trying generic clickable fallback...");
            try {
                WebElement card = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.androidUIAutomator("new UiSelector().clickable(true).instance(" + index + ")")));
                tapElement(card);
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    /**
     * Clicks the "View EMR" button.
     */
    public void clickViewEMR() {
        System.out.println("Searching for 'View EMR' button...");
        
        // Primary: accessibilityId
        // Secondary: descriptionContains (case-insensitive)
        By primary = AppiumBy.accessibilityId("View EMR");
        By secondary = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*View EMR.*\")");
        By tertiary = AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i).*View EMR.*\")");
        By quaternary = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*EMR.*\")");
        
        try {
            WebElement btn;
            try {
                btn = wait.until(ExpectedConditions.elementToBeClickable(primary));
            } catch (Exception e1) {
                try {
                    System.out.println("Primary search failed. Trying description match...");
                    btn = wait.until(ExpectedConditions.elementToBeClickable(secondary));
                } catch (Exception e2) {
                    try {
                        System.out.println("Description search failed. Trying text match...");
                        btn = wait.until(ExpectedConditions.elementToBeClickable(tertiary));
                    } catch (Exception e3) {
                        System.out.println("Text search failed. Trying generic 'EMR' match...");
                        btn = wait.until(ExpectedConditions.elementToBeClickable(quaternary));
                    }
                }
            }
            tapElement(btn);
            System.out.println("'View EMR' clicked successfully!");
        } catch (Exception e) {
            System.err.println("Could not find 'View EMR' button. Attempting a small scroll and retry...");
            performSmallScroll();
            try {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(secondary));
                tapElement(btn);
                System.out.println("'View EMR' clicked successfully after scroll!");
            } catch (Exception e2) {
                System.err.println("CRITICAL: 'View EMR' not found even after scroll.");
                throw e2;
            }
        }
    }

    /**
     * Checks if "Medication" is present in the list.
     */
    public boolean isMedicationPresent() {
        By descLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*Medication.*\")");
        By textLocator = AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i).*Medication.*\")");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(descLocator));
            } catch (Exception e) {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(textLocator));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks on the "Medication" item.
     */
    public void clickMedication() {
        By descLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*Medication.*\")");
        By textLocator = AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i).*Medication.*\")");
        try {
            try {
                WebElement item = wait.until(ExpectedConditions.elementToBeClickable(descLocator));
                tapElement(item);
            } catch (Exception e) {
                WebElement item = wait.until(ExpectedConditions.elementToBeClickable(textLocator));
                tapElement(item);
            }
            System.out.println("'Medication' clicked successfully!");
        } catch (Exception e) {
            System.err.println("Could not click 'Medication'.");
            throw e;
        }
    }

    /**
     * Performs a small scroll down to bring elements into view.
     */
    public void performSmallScroll() {
        int width = driver.manage().window().getSize().getWidth();
        int height = driver.manage().window().getSize().getHeight();
        
        int startX = width / 2;
        int startY = (int) (height * 0.7);
        int endY = (int) (height * 0.4);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
        try { Thread.sleep(1500); } catch (Exception ignored) {}
    }
    
    /**
     * Navigates back.
     */
    public void goBack() {
        driver.navigate().back();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    /**
     * Taps the center of an element using W3C Actions (bypass restricted click).
     */
    private void tapElement(WebElement el) {
        try {
            org.openqa.selenium.Point location = el.getLocation();
            org.openqa.selenium.Dimension size = el.getSize();
            int centerX = location.getX() + (size.getWidth() / 2);
            int centerY = location.getY() + (size.getHeight() / 2);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(Collections.singletonList(tap));
        } catch (Exception e) {
            System.err.println("Fallback to standard click due to tap error: " + e.getMessage());
            el.click();
        }
    }
}
