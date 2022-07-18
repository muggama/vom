package io.vom.appium;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.vom.core.Context;
import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.exceptions.ElementNotFoundException;
import io.vom.exceptions.InfinityLoopException;
import io.vom.exceptions.PlatformNotFoundException;
import io.vom.utils.*;
import org.apache.commons.lang.text.StrSubstitutor;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppiumDriverImpl implements Driver {

    AppiumDriver appiumDriver;
    private Context context;

    private Selector scrollContainer;

    private final Duration scrollDuration = Duration.ofMillis(Integer.parseInt(Properties.getInstance().getProperty("scroll_default_duration_in_millis", "100")));
    private final int scrollLength = Integer.parseInt(Properties.getInstance().getProperty("scroll_length", "300"));

    @Override
    public void prepare(Context context) {
        this.context = context;

        scrollContainer = Objects.requireNonNull(context.getCommonSelector("scroll_container")
                , "scroll container selector was not found in neither resource folder nor repository");

    }

    public AppiumDriverImpl(URL remoteAddress, Capabilities desiredCapabilities) {
        appiumDriver = new AppiumDriver(remoteAddress, desiredCapabilities);
    }

    public AppiumDriverImpl() {
        var prop = Properties.getInstance();

        try {
            var url = new URL(prop.getProperty("appium_url"));
            var reader = new FileReader(FileUtils.getFullPath(prop.getProperty("appium_caps_json_file")));

            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> listPlatforms = gson.fromJson(reader, type);

            var platform = prop.getProperty("appium_platform");
            Objects.requireNonNull(platform, "appium_platform is not prepared on the properties file");
            var map = listPlatforms
                    .stream()
                    .filter((list) -> platform.equalsIgnoreCase((String) list.get("platformName")))
                    .findAny()
                    .orElseThrow(() -> new PlatformNotFoundException("Platform: '" + platform + "' was not found on appium json file"));

            var caps = new DesiredCapabilities(map);
            appiumDriver = new AppiumDriver(url, caps);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Element findElement(Selector selector) {
        try {
            var ae = appiumDriver.findElement(bySelector(selector));

            return new AppiumElementImpl(this, ae);
        } catch (NoSuchElementException e) {
            var exception = new ElementNotFoundException("Element was not found by this selector:" +
                    " name='" + selector.getName() + "' type='" + selector.getType() + "' value='" + selector.getValue() + "'");
            exception.addSuppressed(e);

            throw exception;
        }
    }

    @Override
    public Element findNullableElement(Selector selector) {
        try {
            return findElement(selector);
        } catch (ElementNotFoundException e) {
            return null;
        }
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
                return AppiumBy.xpath(value);
            case "id":
                return AppiumBy.id(value);
            case "class_name":
                return AppiumBy.className(value);
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

    @Override
    public void slipFinger(Point from, Point to, Duration duration) {

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), from.getX(), from.getY()));

        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        sequence.addAction(finger.createPointerMove(duration,
                PointerInput.Origin.viewport(), to.getX(), to.getY()));

        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appiumDriver.perform(List.of(sequence));


    }

    @Override
    public Size getWindowSize() {
        var dim = appiumDriver.manage().window().getSize();

        return new Size(dim.getWidth(), dim.getHeight());
    }

    @Override
    public void removeFocus() {
        var element = findElement(Selector.from("xpath", "//*[@focused='true']"));
        if (element == null) return;

        element.removeFocus();
    }

    @Override
    public void click(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), x, y));

        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        appiumDriver.perform(List.of(sequence));
    }

    @Override
    public void scrollDown() {
        scrollDown(scrollDuration, scrollLength);
    }

    @Override
    public void scrollDown(Duration duration, int length) {
        scrollDown(duration, length, scrollContainer);
    }


    @Override
    public void scrollDown(Duration duration, int length, Selector scrollContainer) {
        scroll(ScrollDirection.DOWN, duration, length, scrollContainer);
    }

    private enum ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private void scroll(ScrollDirection direction, Duration duration, int length, Selector scrollContainer) {
        var elements = Objects.requireNonNull(findElement(scrollContainer).findElements(Selector.from("xpath", "./*"))
                , "it seems that the given view is not scrollable or selector 'scroll_container' does not works in this case");

        if (elements.size() == 0) return;

        int target = elements.size() / 2;

        var element = elements.get(target);

        var from = element.getCenterPoint();

        Point to = null;

        switch (direction) {
            case UP:
                from.move(0, -(element.getSize().getHeight() / 2 - 1));
                to = from.clone()
                        .move(0, length);
                break;
            case DOWN:
                from.move(0, element.getSize().getHeight() / 2 - 1);
                to = from.clone()
                        .move(0, -length);
                break;
            case LEFT:
                from.move(-(element.getSize().getHeight() / 2 - 1), 0);
                to = from.clone()
                        .move(length, 0);
                break;
            case RIGHT:
                from.move(element.getSize().getHeight() / 2 - 1, 0);
                to = from.clone()
                        .move(-length, 0);
                break;
        }

        slipFinger(from, to, duration);

        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void scrollDownTo(String text) {
        scrollDownTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollDownTo(String text, Duration duration, int length) {
        scrollDownTo(text, duration, length, scrollContainer);
    }

    @Override
    public void scrollDownTo(String text, Duration duration, int length, Selector scrollContainer) {
        scrollTo(text, () -> scrollDown(duration, length, scrollContainer));
    }

    private void scrollTo(String text, Runnable runnable) {
        var limit = 50;
        while (!isPresentText(text)) {

            runnable.run();

            limit--;
            if (limit == 0) {
                throw new InfinityLoopException("infinite scrolling, max scroll limit is 50");
            }
        }
    }

    @Override
    public void scrollUp() {
        scrollUp(scrollDuration, scrollLength);
    }

    @Override
    public void scrollUp(Duration duration, int length) {
        scrollUp(duration, length, scrollContainer);
    }

    @Override
    public void scrollUp(Duration duration, int length, Selector scrollContainer) {
        scroll(ScrollDirection.UP, duration, length, scrollContainer);
    }

    @Override
    public void scrollUpTo(String text) {
        scrollUpTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollUpTo(String text, Duration duration, int length) {
        scrollUpTo(text, duration, length, scrollContainer);
    }

    @Override
    public void scrollUpTo(String text, Duration duration, int length, Selector scrollContainer) {
        scrollTo(text, () -> scrollUp(duration, length, scrollContainer));
    }

    @Override
    public void scrollLeft() {
        scrollLeft(scrollDuration, scrollLength);
    }

    @Override
    public void scrollLeft(Duration duration, int length) {
        scrollLeft(duration, length, scrollContainer);
    }

    @Override
    public void scrollLeft(Duration duration, int length, Selector scrollContainer) {
        scroll(ScrollDirection.LEFT, duration, length, scrollContainer);
    }

    @Override
    public void scrollLeftTo(String text) {
        scrollLeftTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollLeftTo(String text, Duration duration, int length) {
        scrollLeftTo(text, duration, length, scrollContainer);
    }

    @Override
    public void scrollLeftTo(String text, Duration duration, int length, Selector scrollContainer) {
        scrollTo(text, () -> scrollLeft(duration, length, scrollContainer));
    }

    @Override
    public void scrollRight() {
        scrollRight(scrollDuration, scrollLength);
    }

    @Override
    public void scrollRight(Duration duration, int length) {
        scrollRight(duration, length, scrollContainer);
    }

    @Override
    public void scrollRight(Duration duration, int length, Selector scrollContainer) {
        scroll(ScrollDirection.RIGHT, duration, length, scrollContainer);
    }

    @Override
    public void scrollRightTo(String text) {
        scrollRightTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollRightTo(String text, Duration duration, int length) {
        scrollRightTo(text, duration, length, scrollContainer);
    }

    @Override
    public void scrollRightTo(String text, Duration duration, int length, Selector scrollContainer) {
        scrollTo(text, () -> scrollRight(duration, length, scrollContainer));
    }

    @Override
    public void scrollDownToEnd() {

    }

    @Override
    public void scrollUpToStart() {

    }

    @Override
    public void scrollLeftToStart() {

    }

    @Override
    public void scrollRightToEnd() {

    }

    @Override
    public boolean isPresentText(String text) {
        Selector selector = Objects.requireNonNull(context.getCommonSelector("present_text"), "present text selector ('present_text') was not found in neither resource folder nor repository");

        HashMap<String, String> map = new HashMap<>();
        map.put("text", text);
        var fixed = StrSubstitutor.replace(selector.getValue(), map);
        var e = findNullableElement(Selector.from(selector.getName(), selector.getType(), fixed));
        return e != null;
    }

    @Override
    public String getPageSource() {
        return appiumDriver.getPageSource();
    }

    @Override
    public byte[] takeScreenshot() {
        return appiumDriver.getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void back() {
        appiumDriver.navigate().back();
    }

    @Override
    public void quit() {
        appiumDriver.quit();
    }

    @Override
    public void close() {
        appiumDriver.close();
    }
}
