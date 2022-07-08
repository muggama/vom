package io.pomtest.tests.core;

import io.pomtest.pages.LoginPage;
import io.pomtest.utils.Reflection;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class PageTest {

    @Test
    public void test_structure() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        final String username = "someUsername";
        final String password = "somePassword";
        String className = Reflection.createProxyObject(LoginPage.class)
                .fillUsername(username)
                .job((it) -> {
                    System.out.println("first job");
                    System.out.println(it.getLocale());
                })
                .fillPassword(password)
                .login()
                .job((it) -> {
                    System.out.println("Second job");
                    System.out.println(it.getLocale());
                })
                .getClass()
                .getSimpleName();

        System.out.println(className);
    }
}
