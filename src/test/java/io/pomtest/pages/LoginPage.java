package io.pomtest.pages;

import io.pomtest.annotations.actions.Clean;
import io.pomtest.annotations.actions.Click;
import io.pomtest.annotations.actions.GetValue;
import io.pomtest.annotations.actions.SetValue;
import io.pomtest.core.Page;

public abstract class LoginPage extends Page<LoginPage> {

    @SetValue("4")
    public abstract LoginPage fillUsername(String name);

    @GetValue("3")
    public abstract LoginPage getUsername(String name);

    @Clean("2")
    abstract LoginPage cleanUsername(String name);

    @SetValue("1")
    public abstract LoginPage fillPassword(String password);

    @Click("cl")
    public abstract MainPage login();
}
