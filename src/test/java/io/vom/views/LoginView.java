package io.vom.views;

import io.vom.annotations.actions.Clear;
import io.vom.annotations.actions.Click;
import io.vom.annotations.actions.GetValue;
import io.vom.annotations.actions.SetValue;
import io.vom.annotations.repositories.Name;
import io.vom.core.Selector;

public abstract class LoginView extends SuperView<LoginView> {

    @Name("dssdsd")
    private Selector username;


    public LoginView fillUsername(String name){
        findElement(username).setText(name);

        return this;
    }

    @GetValue
    @Name("username")
    public abstract String getUsername();

    @Clear
    @Name("username")
    public abstract LoginView cleanUsername();

    @SetValue
    public abstract LoginView fillPassword(String password);

    @Click
    public abstract MainView login();
}
