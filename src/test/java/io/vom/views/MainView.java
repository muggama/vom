package io.vom.views;

import io.vom.annotations.actions.Click;
import io.vom.core.View;

abstract public class MainView extends View<MainView> {

    @Click
    public abstract SuperView<LoginView> clickButton();

}
