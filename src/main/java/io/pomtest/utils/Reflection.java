package io.pomtest.utils;

import java.lang.reflect.InvocationTargetException;

public class Reflection {

    public static <T> T createProxyObject(Class<T> tClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return tClass.getDeclaredConstructor().newInstance();
    }
}
