package io.vom.core;

public interface Element extends Searchable {

    Driver getDriver();

    void setText(String text);

    String getText();

    void clear();

    void click();
}
