# Appium Mobile Automation Setup & Execution Guide

**FCG Corp App Automation**  
**Document Version:** 1.1  
**Prepared By:** Muhammed Irshad K P  
**Date:** 18 March 2026  
**Project:** FCG Corp App  
**Technology:** Appium + Flutter  
**Supported Platforms:** Android & iOS

## 1. Introduction

This document explains the setup and execution flow for the Appium mobile automation framework used for the FCG Corp app.

### Purpose

This document provides the complete setup process for the Appium automation environment and explains how to execute a sample login automation test for the Yard Doctor mobile application.

### Intended Audience

This document can be used by:

- QA Engineers
- Automation Engineers
- Developers
- DevOps Engineers

to replicate the automation setup.

## 2. Framework Scope

The automation framework currently supports:

- Android automation
- iOS automation
- Flutter automation support
- reusable driver creation
- reusable page objects
- permission handling
- cross-platform execution
- Maven execution

Future enhancements may include:

- TestNG integration
- reporting
- CI/CD integration
- parallel execution

## 3. Environment Configuration

This framework has been verified with the following versions:

| Component | Version |
|---|---:|
| Appium Server | 2.4.1 |
| NodeJS | 20.19.0 |
| Java | OpenJDK 21.0.6 |
| UiAutomator2 Driver | 2.45.1 |
| Flutter Driver | 2.18.0 |
| Selenium | 4.9.1 |
| Appium Java Client | 8.5.0 |
| Maven Compiler Plugin | 3.11.0 |

## 4. Java Installation

Install OpenJDK 21 from [Adoptium](https://adoptium.net).

Set:

- `JAVA_HOME`
- `PATH` to include `%JAVA_HOME%\bin`

Verify:

```powershell
java -version
```

Expected:

```text
openjdk version "21.0.6"
```

## 5. Install NodeJS

Install NodeJS LTS from [nodejs.org](https://nodejs.org).

Verify:

```powershell
node -v
npm -v
```

Expected Node version:

```text
v20.19.0
```

## 6. Install Appium

Install Appium globally:

```powershell
npm install -g appium
```

Verify:

```powershell
appium -v
```

Expected:

```text
2.4.1
```

## 7. Install Appium Drivers

Appium 2 requires manual driver installation.

Install UiAutomator2:

```powershell
appium driver install uiautomator2
```

Install Flutter driver:

```powershell
appium driver install flutter
```

Verify:

```powershell
appium driver list
```

Verify:

```powershell
appium driver list
```

Expected output includes:

- `uiautomator2`
- `flutter`
- `xcuitest`

## 8. Android Setup

Install Android Studio and the following components:

- Android SDK
- Android SDK Platform Tools
- Android Emulator

### Configure Android Environment Variables

Set `ANDROID_HOME` to your Android SDK path, for example:

```text
C:\Users\username\AppData\Local\Android\Sdk
```

Add these entries to `PATH`:

- `%ANDROID_HOME%\platform-tools`
- `%ANDROID_HOME%\emulator`
- `%ANDROID_HOME%\tools`
- `%ANDROID_HOME%\tools\bin`

### Verify Android

Verify that the emulator or device is visible:

```powershell
adb devices
```

Expected:

```text
emulator-5554 device
```

### Create Emulator

Create a Pixel emulator using Android Studio Device Manager.

Recommended:

- API Level 33 or newer

## 9. iOS Setup (Mac Only)

iOS automation requires macOS.

### Install Xcode

Install Xcode from the Mac App Store.

Verify:

```powershell
xcodebuild -version
```

Install command line tools:

```powershell
xcode-select --install
```

Accept the license:

```powershell
sudo xcodebuild -license accept
```

### Install CocoaPods

Required for WebDriverAgent.

Install:

```powershell
sudo gem install cocoapods
```

Verify:

```powershell
pod --version
```

### Setup WebDriverAgent

```powershell
cd ~/.appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent
pod install
```

### Start Simulator

```powershell
open -a Simulator
```

Verify:

```powershell
xcrun simctl list devices
```

## 10. Install Maven

Install Maven from [maven.apache.org](https://maven.apache.org/download.cgi).

Set:

- `MAVEN_HOME`
- `PATH` to include `%MAVEN_HOME%\bin`

Verify:

```powershell
mvn -version
```

## 11. Start Appium Server

Start the server:

```powershell
appium
```

Server URL:

```text
http://127.0.0.1:4723
```

## 12. Project Structure

Recommended structure:

```text
appium-test
├── src/main/java
│   ├── AppiumDriverFactory.java
│   ├── LoginPage.java
│   └── LoginTest.java
└── pom.xml
```

## 13. Maven Configuration

Recommended `pom.xml` highlights:

- Java 11 source/target compatibility
- Selenium 4.9.1
- Appium Java Client 8.5.0
- `exec-maven-plugin` for running the test entry point

Recommended execution main class:

```xml
<mainClass>LoginTest</mainClass>
```

## 14. Sample Test Implementation

### Driver Factory

`AppiumDriverFactory` creates the driver based on platform input.

- Android uses `AndroidDriver` + `UiAutomator2Options`
- iOS uses `IOSDriver` + `XCUITestOptions`

The platform is selected from the command-line argument passed to `main()`.

### Login Page

`LoginPage` contains reusable actions for:

- handling the permission popup
- entering Employee ID
- entering Password
- tapping the Log In button

This keeps screen-specific behavior in one place and makes future tests easier to maintain.

### Test Runner

`LoginTest` is now a lightweight runner that:

1. Reads the platform argument
2. Creates the driver
3. Initializes `WebDriverWait`
4. Calls the login page actions
5. Waits briefly so the next screen is visible
6. Quits the session

## 15. Current Login Flow

### Android

The Android flow uses:

- permission button:
  - `com.android.permissioncontroller:id/permission_allow_button`
- Employee ID field:
  - `(//android.widget.EditText)[1]`
- Password field:
  - `(//android.widget.EditText)[2]`
- Login button:
  - `(//android.view.View[@content-desc='Log In'])[2]`

### iOS

The iOS flow is supported through platform branching in code.

Important note:

- the bundle id must be updated to the real iOS app bundle id
- iOS locators may need to be refined once the iOS hierarchy is confirmed

## 16. Sample `LoginTest.java`

```java
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest {

    public static void main(String[] args) throws Exception {
        String platform = args.length > 0 ? args[0].trim().toLowerCase() : "android";

        AppiumDriver driver = AppiumDriverFactory.createDriver(platform);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        LoginPage loginPage = new LoginPage(driver, wait, "ios".equals(platform));
        loginPage.handlePermissionDialogIfPresent();
        loginPage.login("00001879", "1");

        Thread.sleep(4000);
        driver.quit();
    }
}
```

## 17. How to Execute Test

### Android

```powershell
mvn clean compile
mvn exec:java "-Dexec.args=android"
```

### iOS

```powershell
mvn clean compile
mvn exec:java "-Dexec.args=ios"
```

## 18. Expected Result

The test should:

- launch the app automatically
- accept the Android permission popup if shown
- enter Employee ID
- enter Password
- click Log In
- navigate to the next screen
- close the Appium session

## 19. Troubleshooting

### PowerShell argument issue

Use quotes around the Maven argument:

```powershell
mvn exec:java "-Dexec.args=android"
```

### Appium session not starting

Check:

- Appium server is running
- device/emulator is running
- package and activity values are correct

### Element not found

Use Appium Inspector to verify the current UI hierarchy.

Avoid brittle XPath indexes where possible.

## 20. Summary

The automation framework is now organized for reuse and platform support.

The current structure makes it easier to:

- add new tests
- reuse driver creation
- share login behavior
- support both Android and iOS

