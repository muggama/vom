package io.vom.appium;

import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.Selector;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class AppiumElementImpl implements Element {
    private final AppiumDriverImpl driver;
    private final WebElement webElement;

    public AppiumElementImpl(AppiumDriverImpl driver, WebElement webElement) {
        this.driver = driver;
        this.webElement = webElement;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void setText(String text) {
        webElement.click();
        webElement.sendKeys(text);
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public void clear() {
        webElement.clear();
    }

    @Override
    public void click() {
        webElement.click();
    }

    public WebElement getAppiumElement() {
        return webElement;
    }

    @Override
    public Element findElement(Selector selector) {
        return new AppiumElementImpl(driver, webElement.findElement(AppiumDriverImpl.bySelector(selector)));
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return webElement.findElements(AppiumDriverImpl.bySelector(selector))
                .stream()
                .map((e) -> new AppiumElementImpl(driver, e))
                .collect(Collectors.toList());
    }
}
