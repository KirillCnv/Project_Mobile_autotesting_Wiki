package mobile.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class MobileLocalDriverProvider implements WebDriverProvider {
    static MobileDriverConfig config = ConfigFactory.create(MobileDriverConfig.class, System.getProperties());

    public static URL getAppiumServerUrl() {
        try {
            return new URL(config.getLocalUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final WebDriver createDriver(final Capabilities capabilities) {

        File app = getApp();
        UiAutomator2Options options = new UiAutomator2Options();

        options.merge(capabilities);
        options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);

        options.setPlatformName(config.getPlatformName());
        options.setDeviceName(config.getDevice());
        options.setPlatformVersion(config.getOsVersion());

        options.setApp(app.getAbsolutePath());
        options.setAppPackage(config.getAppPackage());
        options.setAppActivity(config.getAppActivity());

        return new AndroidDriver(getAppiumServerUrl(), options);
    }

    private File getApp() {
        String appUrl = config.getAppUrl();
        String appPath = config.getAppPath();

        File app = new File(appPath);
        if (!app.exists()) {
            try (InputStream in = new URL(appUrl).openStream()) {
                copyInputStreamToFile(in, app);
            } catch (IOException e) {
                throw new AssertionError("Failed to download application", e);
            }
        }
        return app;
    }

    public void setMobileDriverConfiguration() {
        Configuration.browser = MobileLocalDriverProvider.class.getName();

        Configuration.browserSize = null;
    }
}