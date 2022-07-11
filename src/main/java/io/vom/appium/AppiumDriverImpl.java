package io.vom.appium;

import io.appium.java_client.AppiumDriver;
import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.Selector;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class AppiumDriverImpl implements Driver {

    AppiumDriver appiumDriver;

    public AppiumDriverImpl(URL remoteAddress, Capabilities desiredCapabilities) {
        appiumDriver = new AppiumDriver(remoteAddress, desiredCapabilities);
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
}
