package io.vom.core;

import java.util.Objects;

public class Selector {
    private String name;
    private String type;
    private String value;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static Selector newSelector(String type,String value){
        Objects.requireNonNull(type);
        Objects.requireNonNull(value);

        var s = new Selector();
        s.type = type;
        s.value = value;

        return s;
    }
}
