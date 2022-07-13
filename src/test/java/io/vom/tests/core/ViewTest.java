package io.vom.tests.core;

import io.vom.appium.AppiumDriverImpl;
import io.vom.core.Context;
import io.vom.utils.Reflection;
import io.vom.views.LoginView;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewTest {


    @Test
    public void context_builder() throws MalformedURLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        URL url = new URL("http://127.0.0.1:4723/wd/hub");

        var caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("udid", "5d66695e");
        caps.setCapability("appActivity", "MainActivity");
        caps.setCapability("appPackage", "io.vom");
        Context context = Context.getBuilder()
                .setDriver(new AppiumDriverImpl(url, caps))
                .build();

        var log = Reflection.createPageObject(context, LoginView.class);

        var username = "hello vom";
        Assert.assertEquals(username,log.fillUsername(username).getUsername());

        log.cleanUsername();

        log.login().job((it) -> System.out.println(it.getContext()));
    }

    @Test
    public void test_buddy() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var loginPage = Reflection.createPageObject(null, LoginView.class);

        Assert.assertEquals("Unfinished handler!!", loginPage.getUsername());

        loginPage.fillPassword("password");

    }
}
