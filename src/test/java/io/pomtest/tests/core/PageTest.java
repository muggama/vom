package io.pomtest.tests.core;

import io.pomtest.pages.LoginPage;
import io.pomtest.utils.Reflection;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class PageTest {

    @Test
    public void test_buddy() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var loginPage = Reflection.createPageObject(LoginPage.class);

        Assert.assertEquals("Unfinished handler!!", loginPage.getUsername());

        var l = loginPage.fillUsername("username");

        Assert.assertEquals(0, l.size());
        l.add("hello world");
        Assert.assertEquals(1, l.size());

        loginPage.fillPassword("password");

    }
}
