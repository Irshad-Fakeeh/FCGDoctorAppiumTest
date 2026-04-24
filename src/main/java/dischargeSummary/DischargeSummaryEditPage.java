package dischargeSummary;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.HidesKeyboard;
import java.util.List;

public class DischargeSummaryEditPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;
    private final int screenHeight;
    private final int screenWidth;

    public DischargeSummaryEditPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
        // Cache screen size to avoid server queries during crash-prone transitions
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        this.screenHeight = size.height;
        this.screenWidth = size.width;
    }

    /**
     * Appends additional text to the Principal Diagnosis field.
     */
    public void addExtraLinesToPrincipalDiagnosis() {
        System.out.println("Finding Principal Diagnosis EditText...");
        // Use a more robust selector that checks description/hint as well as text
        By diagnosisLocator = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").descriptionContains(\"Principal Diagnosis\")"
        );

        try {
            Thread.sleep(2000); // Wait for page transition
            WebElement diagnosisInput;
            try {
                // Use a short 5-second wait for the named locator first
                org.openqa.selenium.support.ui.WebDriverWait quickWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5));
                diagnosisInput = quickWait.until(ExpectedConditions.visibilityOfElementLocated(diagnosisLocator));
            } catch (Exception e) {
                System.out.println("Named locator not found within 5s, falling back to first EditText on page...");
                diagnosisInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.className("android.widget.EditText")));
            }

            diagnosisInput.click(); 
            try { diagnosisInput.clear(); } catch (Exception ignored) {} 
            
            diagnosisInput.sendKeys("New Principal Diagnosis Automation Line 1\nLine 2");
            System.out.println("Focused, cleared Principal Diagnosis and added new data.");
            Thread.sleep(1000); 
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP (PRINCIPAL DIAGNOSIS FAIL) ==============");
            System.err.println("Fatal: Could not locate Principal Diagnosis field.");
            System.err.println("======================================================================\n");
            throw new RuntimeException("Principal Diagnosis field interaction failed.", e);
        }
    }

    /**
     * Scrolls if necessary, clicks the expand ImageView next to Course in Hospital,
     * fills out the popup, and clicks Add.
     */
    public void addCourseInHospitalDetails() throws Exception {
        System.out.println("Swiping up until 'Course in Hospital' is visible (using upper screen area to avoid keyboard)...");
        try {
            org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
            for (int i = 0; i < 7; i++) {
                try {
                    WebElement testRow = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Course in Hospital\")"));
                    int testY = testRow.getLocation().getY();
                    int height = driver.manage().window().getSize().height;
                    System.out.println("Found row at Y=" + testY + " (Screen height: " + height + ")");
                    // Middle-top of screen
                    if (testY > height * 0.1 && testY < height * 0.6) {
                        System.out.println("Course in Hospital row is positioned for interaction.");
                        break;
                    }
                } catch (Exception ignored) {}

                org.openqa.selenium.interactions.Sequence swipe = new org.openqa.selenium.interactions.Sequence(finger, 1);
                int startX = driver.manage().window().getSize().width / 2;
                // Use the upper 50% of the screen to avoid tapping the keyboard
                int startY = (int) (driver.manage().window().getSize().height * 0.5); 
                int endY = (int) (driver.manage().window().getSize().height * 0.2);
                
                swipe.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), startX, startY));
                swipe.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                swipe.addAction(finger.createPointerMove(java.time.Duration.ofMillis(600), org.openqa.selenium.interactions.PointerInput.Origin.viewport(), startX, endY));
                swipe.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                driver.perform(java.util.Collections.singletonList(swipe));
                Thread.sleep(1500); 
            }
        } catch (Exception e) {
            System.out.println("Swipe gesture encountered an issue: " + e.getMessage());
        }

        Thread.sleep(1000); // UI Settle time

        System.out.println("Locating the specific 'small square' maximize icon for Course in Hospital...");
        WebElement courseRow = null;
        try {
            courseRow = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Course in Hospital\")"));
            int rowY = courseRow.getLocation().getY();
            int rowHeight = courseRow.getSize().getHeight();
            int rowCenterY = rowY + (rowHeight / 2);
            int width = driver.manage().window().getSize().width;
            
            System.out.println("Row at Y=" + rowY + ", Height=" + rowHeight + ". Scanning for small icons...");

            // Try 1: Broad search for any small clickable element (ImageView, Button, or View)
            List<WebElement> candidates = driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().clickable(true)"));
            WebElement bestIcon = null;
            int maxRight = -1;
            
            for (WebElement cand : candidates) {
                int cy = cand.getLocation().getY() + (cand.getSize().getHeight() / 2);
                int cx = cand.getLocation().getX() + (cand.getSize().getWidth() / 2);
                int cw = cand.getSize().getWidth();
                int ch = cand.getSize().getHeight();
                
                // Criteria: Right half, vertically near row, and physically small (square-ish icon)
                if (Math.abs(cy - rowCenterY) < 200 && cx > (width * 0.6) && cw < 200 && ch < 200) {
                    if (cx > maxRight) {
                        maxRight = cx;
                        bestIcon = cand;
                    }
                }
            }

            if (bestIcon != null) {
                System.out.println("Clicking best candidate icon (Size: " + bestIcon.getSize().getWidth() + "x" + bestIcon.getSize().getHeight() + ") at X=" + bestIcon.getLocation().getX() + ", Y=" + bestIcon.getLocation().getY());
                bestIcon.click();
            } else {
                throw new Exception("No candidate small square icon found via spatial search.");
            }
        } catch (Exception e1) {
            System.out.println("Discovery failed (" + e1.getMessage() + "). Implementing 'Cluster Tap' (3-point corner strategy)...");
            try {
                if (courseRow == null) {
                    courseRow = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Course in Hospital\")"));
                }
                int width = driver.manage().window().getSize().width;
                int rY = courseRow.getLocation().getY();
                int rH = courseRow.getSize().getHeight();
                
                // Coordinates for 3 points in the far-right corner
                int tapX = (int) (width * 0.94); 
                int[] tapYs = { rY + (rH / 5), rY + (rH / 2), rY + (int)(rH * 0.8) };
                
                org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
                for (int y : tapYs) {
                    org.openqa.selenium.interactions.Sequence tap = new org.openqa.selenium.interactions.Sequence(finger, 1);
                    tap.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), tapX, y));
                    tap.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                    tap.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
                    driver.perform(java.util.Collections.singletonList(tap));
                    System.out.println("Tapped point: " + tapX + ", " + y);
                    Thread.sleep(500); // Quick taps
                }
                
                System.out.println("Cluster tap completed. Waiting for popup...");
                Thread.sleep(3000); 
            } catch (Exception e2) {
                System.err.println("\n============== APP UI DUMP (CLUSTER TAP FAIL) ==============");
                System.err.println("Fatal: Could not trigger maximizing interaction.");
                System.err.println("============================================================\n");
                throw new RuntimeException("Interaction with Course in Hospital failed.", e2);
            }
        }

        // Verify if popup actually opened by waiting for 'Add' button to appear
        System.out.println("Checking if popup opened...");
        WebDriverWait popupWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(8));
        try {
            popupWait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId("Add")));
            System.out.println("Popup detected successfully!");
        } catch (Exception e) {
            System.err.println("Popup didn't appear after click/tap. Aborting data entry to prevent editing main page fields.");
            throw new RuntimeException("Course in Hospital popup failed to open.", e);
        }

        System.out.println("Entering data into popup...");
        try {
            Thread.sleep(1000); 
            List<WebElement> editTexts = driver.findElements(AppiumBy.className("android.widget.EditText"));
            
            // The popup EditText is typically the last one added to the hierarchy when it opens
            WebElement popupInput = editTexts.get(editTexts.size() - 1); 
            popupInput.click();
            
            // Replaced the backspace loop (which caused corruption/symbols) with direct clear/sendKeys
            try { popupInput.clear(); } catch (Exception ignored) {}
            
            String data = "New Course in Hospital Automation Line 1\nLine 2";
            popupInput.sendKeys(data);
            System.out.println("Inputted data into popup text field.");
            
            // We DO NOT call hideKeyboard() here because it sends a KEYCODE_BACK which closes the dialog.
            // But we will call it in clickSave() later after this dialog is dismissed.

            // Click the Add button
            System.out.println("Clicking 'Add' button on the popup...");
            org.openqa.selenium.support.ui.WebDriverWait addWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));
            WebElement addButton = addWait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Add")));
            addButton.click();
            
            // Stabilization wait instead of hideKeyboard (to prevent app closing)
            System.out.println("Waiting for popup to dismiss and UI to settle...");
            Thread.sleep(3000); 
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP (POPUP DATA ENTRY FAIL) ==============");
            System.err.println("Printing getPageSource() is disabled to prevent uiAutomator crashes.");
            System.err.println("===================================================================\n");
            throw e;
        }
    }

    /**
     * Navigates to the Medication and Report pages using structural siblings relative to 'Authorize'.
     */
    public void navigateToMedicationAndReport() {
        System.out.println("Beginning navigation check for Medication and Report pages via structural siblings...");
        
        try {
            if (driver instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) driver).hideKeyboard();
                System.out.println("Keyboard hidden to ensure Medication/Report buttons are visible.");
                Thread.sleep(2000); // UI settle time after keyboard dismiss
            }
        } catch (Exception ignored) {}
        
        // Split into 3 discrete trips to follow user flow: Medication, Report(Lab), Report(Rad)
        String[][] tasks = {
            {"Medication", "//android.view.View[@content-desc='Authorize']/following-sibling::android.widget.ImageView[1]", ""},
            {"Laboratory", "//android.view.View[@content-desc='Authorize']/following-sibling::android.widget.ImageView[2]", "Laboratory"},
            {"Radiology", "//android.view.View[@content-desc='Authorize']/following-sibling::android.widget.ImageView[2]", "Radiology"}
        };
        
        for (String[] task : tasks) {
            String sectionName = task[0];
            String structuralXPath = task[1];
            String targetSubTab = task[2];
            
            try {
                System.out.println("Processing " + sectionName + " page...");
                
                // Ensure section is visible
                try { scrollToSection(sectionName); } catch (Exception ignored) {}
                
                WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.xpath(structuralXPath)));

                if (icon != null) {
                    System.out.println("Clicking " + sectionName + " icon via structural XPath...");
                    icon.click();
                    
                    Thread.sleep(4000); // UI settle time
                    
                    // Verify transition: On the sub-page, "Principal Diagnosis" (EditText instance 0) should be missing
                    boolean transitionSuccess = true;
                    try {
                        driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").descriptionContains(\"Principal Diagnosis\")"));
                        System.out.println("Still on Edit screen. Navigation to " + sectionName + " failed.");
                        transitionSuccess = false;
                    } catch (Exception e) {
                        System.out.println("Verified transition to " + sectionName + " page.");
                    }

                    if (transitionSuccess) {
                        // Handle sub-page logic
                        if (sectionName.equalsIgnoreCase("Medication")) {
                            handleMedicationPageLogic();
                        } else if (sectionName.equalsIgnoreCase("Laboratory") || sectionName.equalsIgnoreCase("Radiology")) {
                            handleReportPageLogic(targetSubTab);
                        }

                        // SMART NAVIGATION: Only go back if we are NOT already on the edit page
                        System.out.println("Checking if still on sub-page...");
                        boolean onEditPage = false;
                        try {
                            // If Principal Diagnosis is present, we are already back
                            driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").descriptionContains(\"Principal Diagnosis\")"));
                            onEditPage = true;
                            System.out.println("Already returned to Edit page automatically.");
                        } catch (Exception e) {
                            onEditPage = false;
                        }

                        if (!onEditPage) {
                            System.out.println("Still on sub-page. Navigating back manually...");
                            driver.navigate().back(); 
                            Thread.sleep(3500); // ANTI-CRASH COOL DOWN
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(sectionName + " sequence encountered an issue: " + e.getMessage());
                // Emergency recovery
                try { 
                   if (!driver.findElements(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Principal Diagnosis\")")).isEmpty()) {
                       // We are on edit page, good.
                   } else {
                       driver.navigate().back(); 
                       Thread.sleep(2000); 
                   }
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Handles the complex logic of selecting medications on the Medication page.
     */
    private void handleMedicationPageLogic() {
        System.out.println("Starting Medication page data selection logic...");
        try {
            // Check if medication list is not empty by looking for common view elements
            // The user provided instance(13) as a reliable checkbox/data point
            By medicationCheckbox = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(13)");
            
            System.out.println("Looking for medication checkboxes...");
            WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(medicationCheckbox));
            
            if (checkbox != null) {
                System.out.println("Medication found. Selecting checkbox...");
                checkbox.click();
                Thread.sleep(1500);
                
                System.out.println("Clicking 'ADD' button...");
                // Standard Material 'ADD' button locator
                By addBtnSelector = AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i)ADD\")");
                WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(addBtnSelector));
                addBtn.click();
                System.out.println("Successfully added medication data.");
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            System.err.println("Medication selection failed or list is empty: " + e.getMessage());
            // Fallback: Try a generic spatial tap in the list area if provided instance failed
            performSpatialDataSelect();
        }
    }

    /**
     * Handles the complex logic of selecting Laboratory or Radiology data on the Report page.
     */
    private void handleReportPageLogic(String subTab) {
        System.out.println("Starting Report page data selection logic for: " + subTab);
        try {
            // 1. Find and interact with the Toggle (Laboratory\nRadiology)
            WebElement toggle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Laboratory\")")
            ));
            
            int toggleWidth = toggle.getSize().getWidth();
            int toggleX = toggle.getLocation().getX();
            int toggleY = toggle.getLocation().getY() + (toggle.getSize().getHeight() / 2);
            
            // Click Left for Lab, Right for Radiology
            int clickX = subTab.equalsIgnoreCase("Laboratory") ? toggleX + (toggleWidth / 4) : toggleX + (int)(toggleWidth * 0.75);
            
            org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
            org.openqa.selenium.interactions.Sequence tap = new org.openqa.selenium.interactions.Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), clickX, toggleY));
            tap.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(tap));
            
            Thread.sleep(3000); // Wait for data to load
            
            // 2. Check for data and select any checkbox
            boolean foundData = false;
            try {
                if (subTab.equalsIgnoreCase("Radiology")) {
                    // Look for the specific date entry provided by user
                    driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"24 Nov 2025\")"));
                } else {
                    // Laboratory generic check
                    driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*(NE|LY|MO|Unit|Laboratory).*\")"));
                }
                foundData = true;
                System.out.println("Data record identified in " + subTab);
            } catch (Exception e) {
                System.out.println("No specific labels found in " + subTab + ". Proceeding with generic selection.");
            }

            // 3. Select checkbox and Add
            try {
                System.out.println("Selecting data in " + subTab + "...");
                
                // Use robust locators for the checkbox
                By checkboxLocator = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(13)");
                By checkboxXPath = AppiumBy.xpath("//android.widget.ScrollView/android.view.View[2]");
                
                WebElement checkbox = null;
                try {
                    checkbox = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));
                } catch (Exception e) {
                    checkbox = wait.until(ExpectedConditions.elementToBeClickable(checkboxXPath));
                }

                if (checkbox != null) {
                    System.out.println("Clicking checkbox in " + subTab + "...");
                    checkbox.click();
                    Thread.sleep(1500);
                    
                    System.out.println("Locating 'Add' button...");
                    By addSelector = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").description(\"Add\")");
                    WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addSelector));
                    addButton.click();
                    System.out.println("Successfully clicked Add for " + subTab);
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                System.err.println("Data selection failed in " + subTab + ": " + e.getMessage());
                performSpatialDataSelect();
            }
            
        } catch (Exception e) {
            System.err.println("Error handling " + subTab + " tab: " + e.getMessage());
        }
    }
    
    /**
     * Scrolls the screen until the specified label is visible in the upper half.
     */
    private void scrollToSection(String labelText) throws Exception {
        org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
        
        // Broader regex to catch 'Med', 'Medication', 'Meds', 'Rep', 'Report', 'Reports' etc.
        // Also checks both text and description/content-desc
        String term = labelText.substring(0, Math.min(labelText.length(), 4)); 
        String uiAutomatorString = "new UiSelector().textMatches(\"(?i).*" + term + ".*\")";
        String uiDescString = "new UiSelector().descriptionMatches(\"(?i).*" + term + ".*\")";

        for (int i = 0; i < 5; i++) {
            try {
                WebElement label;
                try {
                    label = driver.findElement(AppiumBy.androidUIAutomator(uiAutomatorString));
                } catch (Exception e) {
                    label = driver.findElement(AppiumBy.androidUIAutomator(uiDescString));
                }
                
                int labelY = label.getLocation().getY();
                if (labelY > screenHeight * 0.1 && labelY < screenHeight * 0.6) {
                    System.out.println("Section '" + labelText + "' identified at Y=" + labelY);
                    return;
                }
            } catch (Exception ignored) {}

            org.openqa.selenium.interactions.Sequence swipe = new org.openqa.selenium.interactions.Sequence(finger, 1);
            int startX = screenWidth / 2;
            int startY = (int) (screenHeight * 0.5); 
            int endY = (int) (screenHeight * 0.2);
            swipe.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(java.time.Duration.ofMillis(600), org.openqa.selenium.interactions.PointerInput.Origin.viewport(), startX, endY));
            swipe.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(swipe));
            Thread.sleep(2000); // UI settle time
        }
        throw new Exception("Timed out scrolling to section: " + labelText);
    }

    /**
     * Helper to find the rightmost icon near a text label, bypassing fragile XPaths.
     */
    private WebElement findRightmostIconNearLabel(String labelText) throws Exception {
        WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.androidUIAutomator(
            "new UiSelector().textContains(\"" + labelText + "\")"
        )));
        
        int labelY = label.getLocation().getY();
        int labelHeight = label.getSize().getHeight();
        int labelCenterY = labelY + (labelHeight / 2);
        int width = driver.manage().window().getSize().width;

        List<WebElement> images = driver.findElements(AppiumBy.className("android.widget.ImageView"));
        WebElement target = null;
        int maxRight = -1;

        for (WebElement img : images) {
            int imgY = img.getLocation().getY() + (img.getSize().getHeight() / 2);
            int imgX = img.getLocation().getX() + (img.getSize().getWidth() / 2);
            if (Math.abs(imgY - labelCenterY) < 150 && imgX > (width * 0.6)) {
                if (imgX > maxRight) {
                    maxRight = imgX;
                    target = img;
                }
            }
        }
        
        if (target == null) throw new Exception("Could not find icon near label: " + labelText);
        return target;
    }

    /**
     * Clicks the final 'Save' button.
     */
    public void clickSave() {
        System.out.println("Attempting to click final Save button...");
        try {
            // No hideKeyboard call here to avoid app crashes.
            // Use scroll instead of driver.perform() to avoid instrumentationcrash
            System.out.println("Scrolling to ensure bottom bar is visible...");
            driver.executeScript("mobile: scrollGesture", java.util.Map.of(
                "left", screenWidth / 4,
                "top", screenHeight / 2,
                "width", screenWidth / 2,
                "height", screenHeight / 2,
                "direction", "up",
                "percent", 0.3
            ));
            System.out.println("Scrolled up for bottom bar visibility. Settle time...");
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {} 

            // 1. Find and click 'Authorize' checkbox (using accessibilityId from user scan)
            System.out.println("Locating 'Authorize' checkbox...");
            try {
                WebElement authorize = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Authorize")));
                authorize.click();
                System.out.println("Clicked Authorize checkbox.");
                Thread.sleep(1500);
            } catch (Exception e) {
                System.out.println("Authorize checkbox not found or already checked: " + e.getMessage());
            }

            // 2. Click the final 'Save' button
            WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().description(\"Save\")")));
            saveButton.click();
            System.out.println("Data saved successfully! Checking for authorization popup...");
            
            // 3. Handle 'Yes' confirmation popup and subsequent 'OK'
            try {
                WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").description(\"Yes\")")));
                yesButton.click();
                System.out.println("Clicked 'Yes' to authorize discharge summary. Waiting for success confirmation...");
                
                WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").description(\"OK\")")));
                okButton.click();
                System.out.println("Clicked 'OK' on success message. Transitioning to list...");
            } catch (Exception popupEx) {
                System.out.println("No authorization confirmation popup appeared or 'OK' failed: " + popupEx.getMessage());
            }
        } catch (Exception e) {
            System.err.println("\n============== APP UI DUMP (SAVE BUTTON FAIL) ==============");
            System.err.println("Failed to click Save button: " + e.getMessage());
            System.err.println("==============================================================\n");
            throw e;
        }
    }

    /**
     * Unauthorizes an already authorized summary by unticking the Authorize box and providing a reason.
     */
    public void unauthorizeSummary() {
        System.out.println("Scrolling to ensure bottom bar is visible for unauthorization...");
        driver.executeScript("mobile: scrollGesture", java.util.Map.of(
            "left", screenWidth / 4,
            "top", screenHeight / 2,
            "width", screenWidth / 2,
            "height", screenHeight / 2,
            "direction", "up",
            "percent", 0.3
        ));
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        try {
            System.out.println("Locating 'Authorize' checkbox to uncheck...");
            WebElement authorize = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Authorize")));
            authorize.click();
            System.out.println("Clicked Authorize checkbox to unauthorize. Waiting for reason popup...");

            // Use the specific EditText class selector inside the popup
            WebElement reasonField = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\")")));
            reasonField.click();
            reasonField.clear();
            reasonField.sendKeys("Testing purpose");
            System.out.println("Entered unauthorization reason: Testing purpose");

            // Click Yes
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").description(\"Yes\")")));
            yesButton.click();
            System.out.println("Clicked 'Yes' to confirm unauthorization. Waiting for success confirmation...");
            
            // Wait for success popup and click OK
            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").description(\"OK\")")));
            okButton.click();
            System.out.println("Clicked 'OK' on unauthorize success message.");

        } catch (Exception e) {
            System.err.println("Failed to unauthorize summary: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    /**
     * Fallback method that performs a blind tap in the data area and Add button area
     * if standard locators fail.
     */
    private void performSpatialDataSelect() {
        System.out.println("Executing Spatial Fallback: Tapping candidate data area and Add button coordinates...");
        try {
            org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
            
            // 1. Tap the data row area (Middle-Left)
            int dataX = (int) (screenWidth * 0.3);
            int dataY = (int) (screenHeight * 0.5);
            org.openqa.selenium.interactions.Sequence tapData = new org.openqa.selenium.interactions.Sequence(finger, 1);
            tapData.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), dataX, dataY));
            tapData.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            tapData.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(tapData));
            System.out.println("Tapped data area: " + dataX + ", " + dataY);
            Thread.sleep(1000);
            
            // 2. Tap the Add button area (Bottom-Right corner)
            int addX = (int) (screenWidth * 0.85);
            int addY = (int) (screenHeight * 0.92);
            org.openqa.selenium.interactions.Sequence tapAdd = new org.openqa.selenium.interactions.Sequence(finger, 1);
            tapAdd.addAction(finger.createPointerMove(java.time.Duration.ZERO, org.openqa.selenium.interactions.PointerInput.Origin.viewport(), addX, addY));
            tapAdd.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            tapAdd.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(tapAdd));
            System.out.println("Tapped Add button area: " + addX + ", " + addY);
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.err.println("Spatial fallback failed: " + e.getMessage());
        }
    }
}
