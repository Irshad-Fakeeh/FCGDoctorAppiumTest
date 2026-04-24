package dischargeSummary;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.util.Collections;

import java.time.Duration;

public class DischargeSummaryList {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public DischargeSummaryList(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Verifies that the top labels and date filters are visible.
     */
    public void verifyInitialPageElements(String fromDateLabel, String toDateLabel) {
        // Appium's XPath engine often fails with string literals containing newlines.
        // In Flutter, content-desc is perfectly mapped to accessibilityId on Android.
        By authorizedLabelLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i)Unauthorized.*Authorized\")");
        wait.until(ExpectedConditions.visibilityOfElementLocated(authorizedLabelLocator));

        By fromDateLocator = AppiumBy.accessibilityId(fromDateLabel);
        wait.until(ExpectedConditions.visibilityOfElementLocated(fromDateLocator));
        
        By toDateLocator = AppiumBy.accessibilityId(toDateLabel);
        wait.until(ExpectedConditions.visibilityOfElementLocated(toDateLocator));
    }


    /**
     * Taps the right side of the toggle box (Authorized).
     */
    public void clickAuthorizedTab() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // Settle time
        // Using a more robust locator for the toggle that doesn't strictly depend on newline character
        By toggleLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i)Unauthorized.*Authorized\")");
        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(toggleLocator));
        int x = toggle.getLocation().getX() + (int) (toggle.getSize().getWidth() * 0.75);
        int y = toggle.getLocation().getY() + (toggle.getSize().getHeight() / 2);
        tapCoordinate(x, y);
    }

    /**
     * Taps the left side of the toggle box (Unauthorized).
     */
    public void clickUnauthorizedTab() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // Settle time
        By toggleLocator = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i)Unauthorized.*Authorized\")");
        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(toggleLocator));
        int x = toggle.getLocation().getX() + (int) (toggle.getSize().getWidth() * 0.25);
        int y = toggle.getLocation().getY() + (toggle.getSize().getHeight() / 2);
        tapCoordinate(x, y);
    }

    private void tapCoordinate(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    /**
     * Clicks the Edit icon for a discharge summary item.
     * Takes the user to the Discharge Summary Edit page.
     * Returns true if the icon was clicked, false otherwise (e.g. list empty).
     */
    public boolean clickEditIcon() {
        if (isNoDischargeSummaryFound()) {
            System.out.println("No discharge summaries found in the current view. Skipping Edit.");
            return false;
        }

        By editLocator = ios ? AppiumBy.accessibilityId("Edit") 
                             : AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Edit\")");
        try {
            // Increased wait to 15s to handle slower UI refreshes in Flutter
            org.openqa.selenium.support.ui.WebDriverWait extendedWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15));
            WebElement editIcon = extendedWait.until(ExpectedConditions.elementToBeClickable(editLocator));
            editIcon.click();
            return true;
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP ON EDIT ICON FAIL ==============");
            System.err.println("Driver could not find the 'Edit' icon within 15s.");
            if (isNoDischargeSummaryFound()) {
                System.err.println("Note: List appears to have become empty during the wait.");
            }
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("===========================================================\n");
            return false;
        }
    }

    /**
     * Clicks the View icon for a discharge summary item.
     * Takes the user to the Discharge Summary View page.
     */
    public void clickViewIcon() {
        By viewLocator = ios ? AppiumBy.accessibilityId("View") 
                             : AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"View\")");
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));
            WebElement viewIcon = shortWait.until(ExpectedConditions.elementToBeClickable(viewLocator));
            viewIcon.click();
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP ON VIEW ICON FAIL ==============");
            System.err.println("Driver could not find the 'View' icon.");
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("===========================================================\n");
            throw e;
        }
    }

    /**
     * Checks if the "No discharge summary found" view is displayed.
     */
    public boolean isNoDischargeSummaryFound() {
        By emptyStateLocator = AppiumBy.accessibilityId("No discharge summary found");

        // Use a short wait to avoid unnecessary long delays if it's not present
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(emptyStateLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks on the 'From' date element to open the date picker.
     */
    public void clickFromDateToChange(String currentFromDateLabel) {
        By fromDateLocator = AppiumBy.accessibilityId(currentFromDateLabel);
        WebElement fromDateElem = wait.until(ExpectedConditions.elementToBeClickable(fromDateLocator));
        fromDateElem.click();
    }

    /**
     * Interacts with a standard Flutter Material DatePicker to set a new date.
     * Uses the "Switch to input" pencil icon to enter text, then clicks OK.
     */
    public void setDateInPicker(String newDateInput) throws Exception {
        try {
            System.out.println("Deep Scan Diagnostic: Identifying all visible elements in DatePicker...");
            try {
                java.util.List<WebElement> allElements = driver.findElements(AppiumBy.xpath("//*"));
                System.out.println("--- START UI SCAN ---");
                for (WebElement el : allElements) {
                    try {
                        String cls = el.getAttribute("class");
                        String desc = el.getAttribute("content-desc");
                        String txt = el.getAttribute("text");
                        if ((desc != null && !desc.isEmpty()) || (txt != null && !txt.isEmpty())) {
                            System.out.println("Element: [" + cls + "] desc='" + desc + "' text='" + txt + "'");
                        }
                    } catch (Exception ignored) {}
                }
                System.out.println("--- END UI SCAN ---");
            } catch (Exception e) {
                System.err.println("Deep scan failed: " + e.getMessage());
            }

            System.out.println("Allowing DatePicker animation to finish...");
            Thread.sleep(3000); 

            // 1. Dynamic Discovery: Scan for any element that looks like an 'Edit' or 'Input' toggle
            WebElement switchBtn = null;
            try {
                System.out.println("Scanning for interactive icons dynamically...");
                java.util.List<WebElement> descElements = driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\".*\")"));
                for (WebElement el : descElements) {
                    String desc = el.getAttribute("content-desc");
                    if (desc != null) {
                        String d = desc.toLowerCase();
                        if (d.contains("edit") || d.contains("input") || d.contains("pencil") || d.contains("switch") || d.contains("enter")) {
                            System.out.println("DYNAMICALLY FOUND Toggle: " + desc);
                            switchBtn = el;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Dynamic scan failed, falling back to static search...");
            }

            if (switchBtn == null) {
                String[] possibleIds = {"Switch to input", "Edit", "Enter Date", "Switch to text input", "Select Date"};
                for (String pid : possibleIds) {
                    try {
                        switchBtn = driver.findElement(AppiumBy.accessibilityId(pid));
                        if (switchBtn.isDisplayed()) {
                            System.out.println("Found edit icon by static ID: " + pid);
                            break;
                        }
                    } catch (Exception ignored) {}
                }
            }

            if (switchBtn != null) {
                switchBtn.click();
            } else {
                System.out.println("Switch to input failed. Attempting Dynamic Calendar Navigation to " + newDateInput + "...");
                
                // Parse the target date (dd/mm/yyyy)
                String[] parts = newDateInput.split("/");
                String targetDay = parts[0].replaceFirst("^0", ""); // "01" -> "1"
                String targetYear = parts[2];
                
                // Map month number to abbreviation
                String[] monthAbbrs = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                String targetMonth = monthAbbrs[Integer.parseInt(parts[1]) - 1];
                
                System.out.println("Target: Day=" + targetDay + ", Month=" + targetMonth + ", Year=" + targetYear);
                boolean reached = false;
                
                for (int i = 0; i < 48; i++) { // Increase attempts for long spans
                    try {
                        WebElement header = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"20\")"));
                        String currentDesc = header.getAttribute("content-desc");
                        System.out.println("Month/Year Header: " + currentDesc);
                        
                        // Check if we reached the target month and year
                        if (currentDesc != null && currentDesc.contains(targetYear) && currentDesc.contains(targetMonth)) {
                            System.out.println("Target " + targetMonth + " " + targetYear + " reached!");
                            reached = true;
                            break;
                        }
                        
                        // PRIMARY NAVIGATION: Swipe Calendar Body
                        // For simplicity, we swipe backwards. If target is in the past, swipe DOWN.
                        // If target is in the future, we would swipe UP. 
                        // But historically the app starts at current date, so swipe back is usually needed.
                        System.out.println("Performing Calendar Swipe...");
                        performCalendarSwipe(false); 
                        Thread.sleep(1500);

                        // Check if text changed
                        String currentDescCheck = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"20\")")).getAttribute("content-desc");
                        if (currentDescCheck != null && !currentDescCheck.equals(currentDesc)) {
                            System.out.println("Swipe Successful! Header updated to: " + currentDescCheck);
                            continue; 
                        }

                        // SECONDARY NAVIGATION: Sweep Tap
                        int startX = header.getLocation().getX();
                        int centerY = header.getLocation().getY() + (header.getSize().getHeight() / 2);
                        int[] offsets = {-60, -120, -200}; 
                        boolean headerChanged = false;
                        
                        for (int offset : offsets) {
                            int tapX = startX + offset;
                            if (tapX < 50) tapX = 100;
                            
                            System.out.println("Sweep Tap: offset=" + offset + " at (" + tapX + ", " + centerY + ")");
                            tapCoordinate(tapX, centerY);
                            Thread.sleep(1200); 
                            
                            String newDescCheck = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"20\")")).getAttribute("content-desc");
                            if (newDescCheck != null && !newDescCheck.equals(currentDesc)) {
                                System.out.println("Navigation Successful! Header updated to: " + newDescCheck);
                                headerChanged = true;
                                break;
                            }
                        }
                        
                        if (!headerChanged) {
                            System.out.println("Month interaction failed. Attempting Year-Picker Fallback...");
                            header.click();
                            Thread.sleep(1500);
                            try {
                                WebElement yearTarget = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().description(\"" + targetYear + "\")"));
                                yearTarget.click();
                                Thread.sleep(1500);
                            } catch (Exception ignored) {}
                        }
                    } catch (Exception e) {
                        System.err.println("Navigation loop error: " + e.getMessage());
                        break;
                    }
                }
                
                if (reached) {
                    System.out.println("Selecting day " + targetDay + " in " + targetMonth + " " + targetYear + "...");
                    try {
                        WebElement dayElem = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"^" + targetDay + "$\")"));
                        dayElem.click();
                        Thread.sleep(2500); // Increased from 1500ms to ensure dialog state stabilizes
                        
                        System.out.println("Day " + targetDay + " selected. Looking for OK button...");
                        
                        boolean okClicked = false;
                        String[] okSelectors = {
                            "new UiSelector().description(\"OK\")",
                            "new UiSelector().text(\"OK\")",
                            "new UiSelector().className(\"android.widget.Button\").instance(1)"
                        };

                        for (String selector : okSelectors) {
                            try {
                                WebElement okBtn = driver.findElement(AppiumBy.androidUIAutomator(selector));
                                if (okBtn.isDisplayed() && okBtn.isEnabled()) {
                                    okBtn.click();
                                    System.out.println("Clicked OK using: " + selector);
                                    okClicked = true;
                                    break;
                                }
                            } catch (Exception ignored) {}
                        }

                        if (!okClicked) {
                            // Last resort: Spatial Tap if we find NO interactable ID
                            // Tapping slightly higher and more centered (0.75, 0.75)
                            System.out.println("No interactable button found. Attempting Spatial Tap at (0.75, 0.75)...");
                            int w = driver.manage().window().getSize().getWidth();
                            int h = driver.manage().window().getSize().getHeight();
                            tapCoordinate((int)(w * 0.75), (int)(h * 0.75)); 
                            okClicked = true;
                        }
                        
                        System.out.println("DatePicker closed. Stabilizing UI...");
                        Thread.sleep(2000);

                        System.out.println("DatePicker sequence completed.");
                        return; // Done
                    } catch (Exception e) {
                        System.err.println("Final selection failed: " + e.getMessage());
                        throw e; // RE-THROW
                    }
                }
                
                if (!reached) {
                    throw new RuntimeException("Could not navigate to target date via aggressive calendar clicks/swipes.");
                }
            }
            
            // Path B: Switch to Input Mode success -> Enter date and click OK
            System.out.println("Path B: Input Mode enabled. Entering date...");
            Thread.sleep(1500);
            By dateInputLocator = By.className("android.widget.EditText");
            WebElement dateInput = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(dateInputLocator));
            
            dateInput.click();
            dateInput.clear();
            dateInput.sendKeys(newDateInput + "\n");
            System.out.println("Entered date: " + newDateInput);
            
            try {
                if (driver instanceof io.appium.java_client.HidesKeyboard) {
                    ((io.appium.java_client.HidesKeyboard) driver).hideKeyboard();
                    Thread.sleep(1000);
                }
            } catch (Exception ignored) {}

            try {
                driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().description(\"OK\")")).click();
            } catch (Exception e) {
                driver.findElement(AppiumBy.accessibilityId("OK")).click();
            }
            System.out.println("Clicked OK on DatePicker.");
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP ON DATEPICKER FAIL ==============");
            System.err.println("Driver could not interact with the standard Material date picker.");
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("=============================================================\n");
            throw e; // RE-THROW
        }
    }

    public void performSmallScroll() {
        System.out.println("Performing small scroll to ensure Edit icon is visible...");
        int width = driver.manage().window().getSize().getWidth();
        int height = driver.manage().window().getSize().getHeight();
        
        int startX = width / 2;
        int startY = (int) (height * 0.7);
        int endY = (int) (height * 0.4); // 300px swipe on most screens

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.Collections.singletonList(swipe));
        try { Thread.sleep(1500); } catch (Exception ignored) {}
    }

    private void performCalendarSwipe(boolean up) {
        int width = driver.manage().window().getSize().getWidth();
        int height = driver.manage().window().getSize().getHeight();
        
        // Target middle of screen for calendar body
        int startX = width / 2;
        int startY = (int) (height * 0.6);
        int endY = (int) (height * 0.4);
        
        if (!up) { // To go back, we swipe DOWN (finger moves Y small to big)
            startY = (int) (height * 0.4);
            endY = (int) (height * 0.7);
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }
}
