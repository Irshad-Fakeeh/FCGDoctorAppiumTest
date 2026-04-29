import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class ClinicalNotesPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final boolean ios;
    private final String pageName = "Clinical Notes";

    public ClinicalNotesPage(AppiumDriver driver, WebDriverWait wait, boolean ios) {
        this.driver = driver;
        this.wait = wait;
        this.ios = ios;
    }

    /**
     * Navigate through Clinical Notes page:
     * - Click available tabs
     * - Scroll horizontally to view all content
     * - Go back when done
     */
    public void navigateClinicalNotes() {
        System.out.println("[INFO] ===== NAVIGATING CLINICAL NOTES =====");
        
        try {
            // Wait for content to load
            Thread.sleep(1500);
            
            // Click available tabs within Clinical Notes
            clickAvailableTabs();
            
            // Perform horizontal scrolling
            scrollHorizontally(3);
            
            // Go back from Clinical Notes
            goBackFromClinicalNotes();
            
            System.out.println("[INFO] ===== CLINICAL NOTES NAVIGATION COMPLETED =====");
            
        } catch (Exception e) {
            System.out.println("[ERROR] Error navigating Clinical Notes: " + e.getMessage());
            e.printStackTrace();
            try {
                goBackFromClinicalNotes();
            } catch (Exception backEx) {
                System.out.println("[WARNING] Could not go back: " + backEx.getMessage());
            }
        }
    }

    /**
     * Finds and clicks all available tabs within Clinical Notes
     */
    private void clickAvailableTabs() {
        System.out.println("[INFO] Looking for tabs in " + pageName);
        
        try {
            // Find all clickable tab-like elements
            List<WebElement> tabs = driver.findElements(
                    By.xpath("//android.widget.Button | //android.view.View[@clickable='true'] | //android.widget.TextView[@clickable='true']")
            );
            
            System.out.println("[DEBUG] Found " + tabs.size() + " clickable elements");
            
            int tabsClicked = 0;
            for (WebElement tab : tabs) {
                try {
                    String tabText = tab.getText();
                    String tabDesc = tab.getAttribute("content-desc");
                    
                    // Skip if empty
                    if ((tabText == null || tabText.trim().isEmpty()) && 
                        (tabDesc == null || tabDesc.trim().isEmpty())) {
                        continue;
                    }
                    
                    // Skip back/navigation buttons
                    if ((tabText != null && (tabText.contains("Back") || tabText.contains("back"))) ||
                        (tabDesc != null && (tabDesc.contains("Back") || tabDesc.contains("back")))) {
                        continue;
                    }
                    
                    // Skip if it's the page name itself
                    if ((tabText != null && tabText.equalsIgnoreCase(pageName)) ||
                        (tabDesc != null && tabDesc.equalsIgnoreCase(pageName))) {
                        continue;
                    }
                    
                    // Skip already clicked tabs by checking visibility
                    if (!tab.isDisplayed()) {
                        continue;
                    }
                    
                    System.out.println("[DEBUG] Clicking tab: '" + tabText + "' / '" + tabDesc + "'");
                    tab.click();
                    System.out.println("[SUCCESS] Clicked tab: " + (tabText != null ? tabText : tabDesc));
                    
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
            
            System.out.println("[INFO] Total tabs clicked: " + tabsClicked);
            
        } catch (Exception e) {
            System.out.println("[WARNING] Error finding tabs: " + e.getMessage());
        }
    }

    /**
     * Performs horizontal scrolling within Clinical Notes content
     */
    private void scrollHorizontally(int scrollAttempts) {
        System.out.println("[INFO] Starting horizontal scroll in " + pageName + " (" + scrollAttempts + " attempts)");
        
        try {
            if (!ios) {
                int width = driver.manage().window().getSize().getWidth();
                int height = driver.manage().window().getSize().getHeight();
                
                // Scroll in the middle of screen (content area)
                int centerY = (int) (height * 0.5);
                int startX = (int) (width * 0.9);    // Right edge
                int endX = (int) (width * 0.1);      // Left edge
                
                for (int i = 0; i < scrollAttempts; i++) {
                    try {
                        System.out.println("[DEBUG] Scroll attempt " + (i + 1) + "/" + scrollAttempts + " at y=" + centerY);
                        
                        driver.executeScript("mobile: swipeGesture", java.util.Map.ofEntries(
                                java.util.Map.entry("left", startX),
                                java.util.Map.entry("top", centerY),
                                java.util.Map.entry("right", endX),
                                java.util.Map.entry("bottom", centerY),
                                java.util.Map.entry("direction", "left"),
                                java.util.Map.entry("speed", 150)
                        ));
                        
                        Thread.sleep(500);
                        
                    } catch (Exception e) {
                        System.out.println("[DEBUG] Scroll attempt " + (i + 1) + " failed: " + e.getMessage());
                    }
                }
                
                System.out.println("[INFO] Completed horizontal scrolling");
            } else {
                System.out.println("[INFO] Skipping horizontal scroll on iOS");
            }
            
        } catch (Exception e) {
            System.out.println("[WARNING] Error during horizontal scroll: " + e.getMessage());
        }
    }

    /**
     * Goes back from Clinical Notes page
     */
    private void goBackFromClinicalNotes() {
        try {
            System.out.println("[DEBUG] Going back from " + pageName);
            
            if (!ios) {
                ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));
            } else {
                driver.navigate().back();
            }
            
            System.out.println("[SUCCESS] Returned from " + pageName);
            
        } catch (Exception e) {
            System.out.println("[WARNING] Could not go back from " + pageName + ": " + e.getMessage());
        }
    }
}
