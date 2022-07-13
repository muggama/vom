package io.vom.appium;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.appium.java_client.AppiumDriver;
import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.Selector;
import io.vom.utils.FileUtil;
import io.vom.utils.Properties;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppiumDriverImpl implements Driver {

    AppiumDriver appiumDriver;

    public AppiumDriverImpl(URL remoteAddress, Capabilities desiredCapabilities) {
        appiumDriver = new AppiumDriver(remoteAddress, desiredCapabilities);
    }

    public AppiumDriverImpl() {
        var prop = Properties.getInstance();

        try {
            var url = new URL(prop.getProperty("appium_url"));
            var reader = new FileReader(FileUtil.getFullPath(prop.getProperty("appium_caps_json_file")));

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = gson.fromJson(reader, type);

            var caps = new DesiredCapabilities(map);
            appiumDriver = new AppiumDriver(url, caps);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Element findElement(Selector selector) {
        return new AppiumElementImpl(this, appiumDriver.findElement(bySelector(selector)));
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return appiumDriver.findElements(bySelector(selector))
                .stream()
                .map((e) -> new AppiumElementImpl(this, e))
                .collect(Collectors.toList());
    }

    static By bySelector(Selector selector) {
        Objects.requireNonNull(selector, "Selector must not be null");
        String value = selector.getValue();

        switch (selector.getType().toLowerCase()) {
            case "xpath":
                return By.xpath(value);
            case "id":
                return By.id(value);
            case "class_name":
                return By.className(value);
            default:
                throw new UnsupportedOperationException("Unsupported selector type");
        }
    }

    @Override
    public String getPlatform() {
        return appiumDriver.getCapabilities()
                .getCapability("platformName")
                .toString()
                .toLowerCase();
    }
}
