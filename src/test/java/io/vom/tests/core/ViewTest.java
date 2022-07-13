package io.vom.tests.core;

import io.vom.appium.AppiumDriverImpl;
import io.vom.core.Context;
import io.vom.views.LoginView;
import org.junit.Assert;
import org.junit.Test;

public class ViewTest {


    @Test
    public void context_builder() {
        Context context = Context.getBuilder()
                .setDriver(new AppiumDriverImpl())
                .build();

        var log = context.loadView(LoginView.class);

        var username = "hello vom";
        Assert.assertEquals(username, log.fillUsername(username).getUsername());

        log.cleanUsername();

        log.job((it) -> Assert.assertEquals("Username", it.getUsername())).login();
    }
}
