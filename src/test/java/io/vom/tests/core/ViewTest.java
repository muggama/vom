package io.vom.tests.core;

import io.vom.core.Context;
import io.vom.utils.Reflection;
import io.vom.views.LoginView;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class ViewTest {

    @Test
    public void context_builder(){

       var e =  Assert.assertThrows(NullPointerException.class,() -> Context.getBuilder()
               .setDriver(null)
               .build());

       Assert.assertEquals("when driver is null system allows to create new Context instance",
               "Driver is null, you should set Driver to start project",e.getMessage());
    }

    @Test
    public void test_buddy() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var loginPage = Reflection.createPageObject(null,LoginView.class);

        Assert.assertEquals("Unfinished handler!!", loginPage.getUsername());

        var l = loginPage.fillUsername("username");

        Assert.assertEquals(0, l.size());
        l.add("hello world");
        Assert.assertEquals(1, l.size());

        loginPage.fillPassword("password");

    }
}
