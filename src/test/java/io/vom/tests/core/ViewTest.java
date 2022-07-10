package io.vom.tests.core;

import io.vom.views.LoginView;
import io.vom.utils.Reflection;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class ViewTest {

    @Test
    public void test_buddy() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var loginPage = Reflection.createPageObject(LoginView.class);

        Assert.assertEquals("Unfinished handler!!", loginPage.getUsername());

        var l = loginPage.fillUsername("username");

        Assert.assertEquals(0, l.size());
        l.add("hello world");
        Assert.assertEquals(1, l.size());

        loginPage.fillPassword("password");

    }
}
