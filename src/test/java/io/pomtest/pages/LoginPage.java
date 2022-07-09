package io.pomtest.pages;

import io.pomtest.annotations.actions.Clean;
import io.pomtest.annotations.actions.Click;
import io.pomtest.annotations.actions.GetValue;
import io.pomtest.annotations.actions.SetValue;
import io.pomtest.core.Page;

import java.util.ArrayList;

public abstract class LoginPage extends SuperPage<LoginPage> {
    @SetValue("4")
    public abstract ArrayList<String> fillUsername(String name);

    @GetValue(value = "3")
    public abstract String getUsername();

    @Clean("2")
    public abstract LoginPage cleanUsername();

    @SetValue("1")
    public abstract LoginPage fillPassword(String password);

    @Click("cl")
    public abstract MainPage login();
}
