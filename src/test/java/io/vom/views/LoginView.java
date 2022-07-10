package io.vom.views;

import io.vom.annotations.actions.Clean;
import io.vom.annotations.actions.Click;
import io.vom.annotations.actions.GetValue;
import io.vom.annotations.actions.SetValue;

import java.util.ArrayList;

public abstract class LoginView extends SuperView<LoginView> {
    @SetValue("4")
    public abstract ArrayList<String> fillUsername(String name);

    @GetValue(value = "3")
    public abstract String getUsername();

    @Clean("2")
    public abstract LoginView cleanUsername();

    @SetValue("1")
    public abstract LoginView fillPassword(String password);

    @Click("cl")
    public abstract MainView login();
}
