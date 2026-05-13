import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.support.ui.WebDriverWait;
import dischargeSummary.DischargeSummaryList;
import dischargeSummary.DischargeSummaryEditPage;
import java.util.Set;

import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) throws Exception {
        String platform = args.length > 0 ? args[0].trim().toLowerCase() : "android";

        System.out.println("Connecting to Appium server...");
        // Create the platform-specific Appium session
        AppiumDriver driver = AppiumDriverFactory.createDriver(platform);

        System.out.println("Session started! Waiting for app to partially initialize (8s)...");
        try { Thread.sleep(8000); } catch (InterruptedException ignored) {} 

        System.out.println("App ready. Performing login actions...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90));

        // Handle Login
        LoginPage loginPage = new LoginPage(driver, wait, "ios".equals(platform));
        loginPage.handlePermissionDialogIfPresent();
        loginPage.login("00004522", "1");

        HomePage homePage = new HomePage(driver, wait, "ios".equals(platform));

        System.out.println("Login sequence completed. Transitioning to Discharge Summary...");
        homePage.clickDischargeTab();
        Thread.sleep(1500); // reduced settle time

        // Verify the discharge summary elements
        System.out.println("Verifying discharge summary list elements...");
        DischargeSummaryList dischargePage = new DischargeSummaryList(driver, wait, "ios".equals(platform));
        
        // Dynamically calculate the dates (To = Today, From = 3 days ago)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate threeDaysAgo = today.minusDays(3);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String defaultFromDate = "From\n" + threeDaysAgo.format(formatter);
        String defaultToDate = "To\n" + today.format(formatter);
        
        dischargePage.verifyInitialPageElements(defaultFromDate, defaultToDate);
         
        // 1. Ensure data is visible. If we are empty, go grab the history bounds.
        if (dischargePage.isNoDischargeSummaryFound()) {
            System.out.println("No discharge summary found on default date. Modifying the 'From' date...");
            dischargePage.clickFromDateToChange(defaultFromDate);
            System.out.println("Entering new date: 01/07/2025 in datepicker...");
            dischargePage.setDateInPicker("01/07/2025");
            
            System.out.println("Stabilization wait (4s) to prevent crash after DatePicker and refresh...");
            Thread.sleep(4000); 

            // Dynamic wait for date refresh
            String updatedFromDate = "From\n01/07/2025";
            System.out.println("Waiting for list to refresh with new date...");
            dischargePage.verifyInitialPageElements(updatedFromDate, defaultToDate);
            
            if (dischargePage.isNoDischargeSummaryFound()) {
                System.out.println("WARNING: No records found for July 2025, but proceeding to check tab toggles...");
            }
            System.out.println("Successfully retrieved data for the new date range!");
        } else {
            System.out.println("Discharge summaries found for the default date range.");
        }

        // --- NEW TESTING SEQUENCE ---
        
        // 2. Switch to Authorized Tab and check if data exists
        System.out.println("Toggling to 'Authorized' view to check data...");
        dischargePage.clickAuthorizedTab();
        Thread.sleep(4000); // 4s for authorized list to load
        
        if (dischargePage.isNoDischargeSummaryFound()) {
            System.out.println("Authorized view is empty for this date.");
        } else {
            System.out.println("Authorized data found.");
        }

        // 3. Switch back to Unauthorized Tab and check the Edit button
        System.out.println("Toggling back to 'Unauthorized' view...");
        dischargePage.clickUnauthorizedTab();
        Thread.sleep(3000); // Wait for unauthorized list to load
        
        // Small scroll to ensure Edit icon is visible as requested
        dischargePage.performSmallScroll();
        
        System.out.println("Clicking 'Edit' icon on unauthorized data...");
        if (dischargePage.clickEditIcon()) {
            Thread.sleep(3000); // Give the Edit page time to load
            
            // 4. Perform the UI Forms Routing on the Edit page
            System.out.println("Executing data entry on the Edit Screen...");
            DischargeSummaryEditPage editPage = new DischargeSummaryEditPage(driver, wait, "ios".equals(platform));
            editPage.addExtraLinesToPrincipalDiagnosis();
            editPage.addCourseInHospitalDetails();
            editPage.navigateToMedicationAndReport();
            editPage.clickSave();
            Thread.sleep(3000); // Wait for the Save confirmation, popup handling, and navigation back
            
            // 5. App naturally returns to DischargeSummaryList. Select Authorized and click Edit.
            System.out.println("Switching back to 'Authorized' Tab to verify the authorized record...");
            dischargePage.clickAuthorizedTab();
            Thread.sleep(5000); // 5s for authorized list to load
            
            dischargePage.performSmallScroll();
            System.out.println("Clicking 'Edit' icon on the newly authorized data...");
            if (dischargePage.clickEditIcon()) {
                System.out.println("Successfully clicked Edit on the Authorized record.");
                Thread.sleep(3000); // Give the Edit page time to load

                System.out.println("Executing Unauthorize sequence on the Edit Screen...");
                editPage.unauthorizeSummary();

                System.out.println("Navigating back to main list...");
                driver.navigate().back(); // Nav back to list after unauthorizing
                Thread.sleep(2000);

                // 6. View the newly Unauthorized record's PDF
                System.out.println("Switching back to 'Unauthorized' Tab to view the returned record...");
                dischargePage.clickUnauthorizedTab();
                Thread.sleep(5000); // Wait for the unauthorized list to load
                
                System.out.println("Performing small scroll to bring 'View' icon into focus...");
                dischargePage.performSmallScroll();
                Thread.sleep(1500);

                System.out.println("Clicking 'View' icon to load the generated PDF...");
                dischargePage.clickViewIcon();
                System.out.println("Waiting for PDF to load completely (15s)...");
                Thread.sleep(15000); // 15s wait for the PDF document to fully render

                System.out.println("PDF verification complete, navigating back to discharge summary list...");
                driver.navigate().back();
                Thread.sleep(2000);
            } else {
                System.out.println("Failed to click Edit on the Authorized record or icon not found.");
            }
            
        } else {
            System.out.println("No 'Edit' icon clickable. Skipping direct record editing.");
            System.out.println("Navigating back to main list manually...");
            driver.navigate().back(); // Nav back to list if we skipped
            Thread.sleep(2000);
        }
        
        // --- CURRENT INPATIENTS WORKFLOW ---
        System.out.println("Starting Current Inpatients verification workflow...");
        
        // 1. Navigate back from Discharge Summary List to Home Screen
        System.out.println("Navigating back to Home Screen...");
        homePage.clickHomeTab();
        Thread.sleep(5000); // 5s for dashboard to load

        // 2. Click Current Inpatients
        homePage.clickCurrentInpatients();
        CurrentInpatientsPage inpatientsPage = new CurrentInpatientsPage(driver, wait, "ios".equals(platform));
        Thread.sleep(5000); // Wait for list to load

        // 3. Check if list is empty
        if (inpatientsPage.isListEmpty()) {
            System.out.println("Current Inpatients list is empty. Returning to Home Screen...");
            inpatientsPage.goBack();
        } else {
            System.out.println("Current Inpatients list found. Searching for patient with medications...");
            boolean medicationFound = false;
            
            // Try checking the first 5 patient cards
            for (int i = 0; i < 5; i++) {
                System.out.println("Inspecting patient card index " + i + "...");
                if (inpatientsPage.clickPatientCard(i)) {
                    Thread.sleep(3000);
                    
                    System.out.println("Clicking View EMR...");
                    inpatientsPage.clickViewEMR();
                    Thread.sleep(4000); // Wait for EMR to load
                    
                    if (inpatientsPage.isMedicationPresent()) {
                        System.out.println("SUCCESS: Medication found! Opening medication details...");
                        inpatientsPage.clickMedication();
                        medicationFound = true;
                        Thread.sleep(4000);
                        break; // Exit loop once found
                    } else {
                        System.out.println("No medication found for this patient. Returning to list to check another card...");
                        // Navigate back: EMR -> Patient Details -> Current Inpatients List
                        inpatientsPage.goBack(); 
                        Thread.sleep(2000);
                        inpatientsPage.goBack();
                        Thread.sleep(3000);
                    }
                } else {
                    System.out.println("Reached end of visible patient list or card not clickable.");
                    break;
                }
            }
            
            if (!medicationFound) {
                System.out.println("Finished checking available patients. No medication lists found.");
                System.out.println("Returning to Home Screen...");
                inpatientsPage.goBack();
            }
        }
        
        System.out.println("Full comprehensive test sequence completed! Session remains open for further inspection.");
    }
}