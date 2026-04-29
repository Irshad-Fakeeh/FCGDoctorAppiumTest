import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.Arrays;
import java.util.List;

public class EMRPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;

    public EMRPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Main method to navigate through EMR sections: clinical notes, vitals, lab,
     * radiology, medication, surgical records.
     * Clicks each section if available, otherwise skips to the next.
     * Handles patients with partial EMR data.
     */
    public void navigateEMRSections() {
        List<String> sections = Arrays.asList(
                "Clinical Notes",
                "Vitals",
                "Lab",
                "Radiology Reports",
                "Current Medication",
                "Surgical Records");

        System.out.println("[INFO] ===== STARTING EMR SECTION NAVIGATION =====");
        for (String section : sections) {
            System.out.println("\n[INFO] ##### ATTEMPTING SECTION: " + section + " #####");
            scrollToSection(section); // Try to scroll to section first
            boolean found = clickEMRSection(section);

            if (!found) {
                System.out.println(
                        "[INFO] " + section + " not found (1 attempt used). Aborting EMR iteration and returning.");
                break;
            }
        }

        System.out.println("[INFO] ===== COMPLETED EMR SECTION NAVIGATION =====");
        goBackFromEMR();
    }

    /**
     * Scrolls to find the EMR section (in case tabs/buttons are in a scrollable
     * container)
     */
    private void scrollToSection(String sectionName) {
        try {
            if (!ios) {
                // Direct check - is the section visible?
                List<WebElement> found = driver.findElements(
                        By.xpath("//*[contains(@text, '" + sectionName + "')] | //*[contains(@content-desc, '"
                                + sectionName + "')]"));
                if (!found.isEmpty()) {
                    System.out.println("[DEBUG] ✓ Section '" + sectionName + "' already visible on screen");
                    return;
                }

                System.out.println("[DEBUG] ✗ Section '" + sectionName + "' not visible, attempting to scroll down...");

                // Try to scroll vertically (Down) to find the section tab (1 attempt)
                for (int i = 0; i < 1; i++) {
                    System.out.println(
                            "[DEBUG] Scroll attempt " + (i + 1) + ": scrolling down to find '" + sectionName + "'");
                    scrollDown(); // Scroll DOWN (swipe up) to reveal lower tabs
                    Thread.sleep(800);

                    found = driver.findElements(
                            By.xpath(
                                    "//*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                            + sectionName.toLowerCase()
                                            + "')] | //*[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                            + sectionName.toLowerCase() + "')]"));
                    if (!found.isEmpty()) {
                        System.out.println("[DEBUG] ✓ Found section '" + sectionName + "' after scrolling down");
                        return;
                    }
                }

                System.out.println("[DEBUG] Section still not found, attempting to scroll up...");
                // Try to scroll vertically (Up) to find the section tab (1 attempt)
                for (int i = 0; i < 1; i++) {
                    System.out.println(
                            "[DEBUG] Scroll attempt " + (i + 1) + ": scrolling up to find '" + sectionName + "'");
                    scrollUp(); // Scroll UP (swipe down) to reveal upper tabs
                    Thread.sleep(800);

                    found = driver.findElements(
                            By.xpath(
                                    "//*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                            + sectionName.toLowerCase()
                                            + "')] | //*[contains(translate(@content-desc,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '"
                                            + sectionName.toLowerCase() + "')]"));
                    if (!found.isEmpty()) {
                        System.out.println("[DEBUG] ✓ Found section '" + sectionName + "' after scrolling up");
                        return;
                    }
                }

                System.out.println("[DEBUG] ⚠ Could not find '" + sectionName + "' after scrolling both ways");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] scrollToSection error: " + e.getMessage());
        }
    }

    /**
     * Scrolls DOWN (content moves up) to reveal more tabs/sections at the bottom
     */
    private void scrollDown() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();

            int centerX = (int) (width * 0.5);
            int startY = (int) (height * 0.8); // Start near bottom
            int endY = (int) (height * 0.2); // Swipe up to top

            driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                    java.util.Map.entry("left", centerX - 50),
                    java.util.Map.entry("top", endY),
                    java.util.Map.entry("width", 100),
                    java.util.Map.entry("height", startY - endY),
                    java.util.Map.entry("direction", "up"),
                    java.util.Map.entry("percent", 0.75),
                    java.util.Map.entry("speed", 400)));
        } catch (Exception e) {
            System.out.println("[DEBUG] Scroll DOWN failed: " + e.getMessage());
        }
    }

    /**
     * Scrolls UP (content moves down) to reveal more tabs/sections at the top
     */
    private void scrollUp() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            int height = driver.manage().window().getSize().getHeight();

            int centerX = (int) (width * 0.5);
            int startY = (int) (height * 0.2); // Start near top
            int endY = (int) (height * 0.8); // Swipe down to bottom

            driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                    java.util.Map.entry("left", centerX - 50),
                    java.util.Map.entry("top", startY),
                    java.util.Map.entry("width", 100),
                    java.util.Map.entry("height", endY - startY),
                    java.util.Map.entry("direction", "down"),
                    java.util.Map.entry("percent", 0.75),
                    java.util.Map.entry("speed", 400)));
        } catch (Exception e) {
            System.out.println("[DEBUG] Scroll UP failed: " + e.getMessage());
        }
    }

    /**
     * Attempts to click an EMR section by name. If found, clicks and waits for
     * content to load, then goes back.
     * If not found, logs a warning and continues to the next section.
     * Handles dynamic availability - some patients may not have all sections.
     * 
     * @return boolean true if successfully found and clicked, false otherwise
     */
    private boolean clickEMRSection(String sectionName) {

        System.out.println("\n[STEP] Opening section: " + sectionName);

        By locator = By.xpath(
                "//*[contains(@content-desc,'" + sectionName + "') or contains(@text,'" + sectionName + "')]");

        List<WebElement> elements = driver.findElements(locator);

        // ✅ Handle not found
        if (elements.isEmpty()) {
            System.out.println("[INFO] Section not found: " + sectionName);
            return false;
        }

        boolean clicked = false;
        try {
            for (WebElement section : elements) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(section)).click();
                    clicked = true;
                    break;
                } catch (Exception ex) {
                    // Try next element if this one is not clickable
                }
            }

            if (!clicked) {
                System.out.println("[WARNING] Found elements for " + sectionName + " but none were clickable!");
                return false;
            }

            System.out.println("[SUCCESS] Clicked: " + sectionName);

            Thread.sleep(2000);

            // 🔥 Special handling for Clinical Notes
            if (sectionName.trim().equalsIgnoreCase("Clinical Notes")) {
                System.out.println("[INFO] Handling Clinical Notes...");
                ClinicalNotesPage clinicalNotesPage = new ClinicalNotesPage(driver, wait, ios);
                clinicalNotesPage.navigateClinicalNotes();
                return true; // Clinical Notes has its own back handling, so we return here
            } else if (sectionName.trim().equalsIgnoreCase("Vitals")) {
                System.out.println("[INFO] Handling Vitals...");
                Vitals vitalsPage = new Vitals(driver, wait, ios);
                vitalsPage.navigateVitals();
                return true;
            } else if (sectionName.trim().equalsIgnoreCase("Lab")) {
                System.out.println("[INFO] Handling Lab...");
                LabPage labPage = new LabPage(driver, wait, ios);
                labPage.navigateLab();
                return true;
            } else if (sectionName.trim().equalsIgnoreCase("Radiology Reports")) {
                System.out.println("[INFO] Handling Radiology Reports...");
                RadiologyReportsPage radiologyReportsPage = new RadiologyReportsPage(driver, wait, ios);
                radiologyReportsPage.navigateRadiologyReports();
                return true;
            } else if (sectionName.trim().equalsIgnoreCase("Current Medication")) {
                System.out.println("[INFO] Handling Current Medication...");
                CurrentMedicationPage medicationPage = new CurrentMedicationPage(driver, wait, ios);
                medicationPage.navigateCurrentMedication();
                return true;
            } else if (sectionName.trim().equalsIgnoreCase("Surgical Records")) {
                System.out.println("[INFO] Handling Surgical Records...");
                SurgicalRecordsPage surgicalRecordsPage = new SurgicalRecordsPage(driver, wait, ios);
                surgicalRecordsPage.navigateSurgicalRecords();
                return true;
            } else {
                // Generic handling
                handleGenericSection(sectionName);
            }

            Thread.sleep(2000);
            // ✅ Go back after action
            goBackFromSection();
            Thread.sleep(1000);

            return true;

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click " + sectionName + ": " + e.getMessage());
            return false;
        }
    }

    private void handleGenericSection(String sectionName) {

        System.out.println("[INFO] Handling section: " + sectionName);

        try {
            clickAvailableTabsInSection(sectionName);
            scrollHorizontallyInSection(2);
        } catch (Exception e) {
            System.out.println("[DEBUG] No additional actions for: " + sectionName);
        }
    }

    /**
     * Finds and clicks all available tabs within the current EMR section
     */
    private void clickAvailableTabsInSection(String sectionName) {
        System.out.println("[INFO] Looking for tabs within section: " + sectionName);

        try {
            // Find all clickable tab-like elements (buttons, text views with specific
            // patterns)
            List<WebElement> tabs = driver.findElements(
                    By.xpath(
                            "//android.widget.Button | //android.view.View[@clickable='true'] | //android.widget.TextView[@clickable='true']"));

            System.out.println("[DEBUG] Found " + tabs.size() + " clickable elements");

            int tabsClicked = 0;
            for (WebElement tab : tabs) {
                try {
                    String tabText = tab.getText();
                    String tabDesc = tab.getAttribute("content-desc");

                    // Skip if empty or if it's a navigation button
                    if ((tabText == null || tabText.trim().isEmpty()) &&
                            (tabDesc == null || tabDesc.trim().isEmpty())) {
                        continue;
                    }

                    // Skip back buttons and common navigation elements
                    if ((tabText != null && (tabText.contains("Back") || tabText.contains("back"))) ||
                            (tabDesc != null && (tabDesc.contains("Back") || tabDesc.contains("back")))) {
                        continue;
                    }

                    // Skip if already clicked (same as section name)
                    if ((tabText != null && tabText.equalsIgnoreCase(sectionName)) ||
                            (tabDesc != null && tabDesc.equalsIgnoreCase(sectionName))) {
                        continue;
                    }

                    System.out.println("[DEBUG] Clicking tab: '" + tabText + "' / '" + tabDesc + "'");
                    tab.click();
                    System.out.println("[SUCCESS] Clicked tab in " + sectionName);

                    Thread.sleep(1500);
                    tabsClicked++;

                    if (tabsClicked >= 3) {
                        System.out.println("[INFO] Clicked " + tabsClicked + " tabs, stopping");
                        break;
                    }

                } catch (Exception e) {
                    System.out.println("[DEBUG] Could not click tab: " + e.getMessage());
                }
            }

            System.out.println("[INFO] Total tabs clicked in section: " + tabsClicked);

        } catch (Exception e) {
            System.out.println("[WARNING] Error finding tabs: " + e.getMessage());
        }
    }

    /**
     * Performs horizontal scrolling within the current section content
     */
    private void scrollHorizontallyInSection(int scrollAttempts) {
        System.out.println("[INFO] Attempting horizontal scroll within section (" + scrollAttempts + " attempts)");

        try {
            if (!ios) {
                int width = driver.manage().window().getSize().getWidth();
                int height = driver.manage().window().getSize().getHeight();

                // Scroll in the middle of the screen (content area)
                int centerY = (int) (height * 0.5);
                int startX = (int) (width * 0.9);
                int endX = (int) (width * 0.1);

                for (int i = 0; i < scrollAttempts; i++) {
                    try {
                        System.out.println("[DEBUG] Horizontal scroll attempt " + (i + 1) + " at y=" + centerY);

                        driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                                java.util.Map.entry("left", startX),
                                java.util.Map.entry("top", centerY),
                                java.util.Map.entry("right", endX),
                                java.util.Map.entry("bottom", centerY),
                                java.util.Map.entry("direction", "left"),
                                java.util.Map.entry("speed", 150)));

                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.out.println("[DEBUG] Scroll attempt " + (i + 1) + " failed: " + e.getMessage());
                    }
                }

                System.out.println("[INFO] Completed horizontal scrolling in section");
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Error during horizontal scroll: " + e.getMessage());
        }
    }

    /**
     * Goes back from an EMR section by pressing the back key or using navigation.
     */
    private void goBackFromSection() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned from EMR section");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from EMR section: " + e.getMessage());
        }
    }

    /**
     * Goes back from EMR main page by pressing the back key.
     */
    public void goBackFromEMR() {
        try {
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            System.out.println("[SUCCESS] Returned from EMR page");
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from EMR: " + e.getMessage());
        }
    }
}
