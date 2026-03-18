import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.URL;

public final class AppiumDriverFactory {

    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";

    private AppiumDriverFactory() {
        // Utility class
    }

    public static AppiumDriver createDriver(String platform) throws Exception {
        if (isIos(platform)) {
            XCUITestOptions options = new XCUITestOptions();
            options.setPlatformName("iOS");
            options.setDeviceName("iPhone");
            options.setAutomationName("XCUITest");
            options.setBundleId("com.dsfh.yardoctorapp"); // replace with the real iOS bundle id
            options.setNoReset(false);
            return new IOSDriver(new URL(APPIUM_SERVER_URL), options);
        }

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("emulator-5554");
        options.setAppPackage("com.dsfh.yardoctorapp.beta");
        options.setAppActivity("com.dsfh.yardoctorapp.MainActivity");
        options.setNoReset(false);
        return new AndroidDriver(new URL(APPIUM_SERVER_URL), options);
    }

    private static boolean isIos(String platform) {
        return "ios".equalsIgnoreCase(platform);
    }
}
