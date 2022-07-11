package io.vom.utils;

import io.vom.annotations.actions.*;
import io.vom.core.Context;
import io.vom.core.View;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Reflection {

    private static final Map<Class<? extends Annotation>, InvocationHandler> actionHandler = new HashMap<>();

    static {
        actionHandler.put(GetValue.class, (self, method, objects) -> "Unfinished handler!!");

        actionHandler.put(SetValue.class, (self, method, objects) -> {
            SetValue annotation = method.getDeclaredAnnotation(SetValue.class);

            System.out.println(objects[0]);
            if (method.getReturnType().isAssignableFrom(self.getClass())) {
                return self;
            } else if (method.getReturnType() == void.class) {
                return void.class;
            } else {
                if (annotation.returnType() != Void.class && annotation.returnType().isAssignableFrom(method.getReturnType())) {
                    return annotation.returnType().getDeclaredConstructor().newInstance();
                } else {
                    try {
                        if (annotation.returnType() != Void.class) throw new Exception();
                        return method.getReturnType().getDeclaredConstructor().newInstance();
                    } catch (Throwable e) {
                        throw new UnsupportedOperationException("Method: '" + method.getName() + "'s return type is invalid in this situation");
                    }
                }
            }
        });

        actionHandler.put(Clear.class, (self, method, objects) -> {
            // unfinished
            if (method.getReturnType() == self.getClass()) {
                return self;
            } else if (method.getReturnType() == Void.class) {
                return new Object();
            } else {
                throw new UnsupportedOperationException(" Return type is invalid in this situation");
            }
        });
    }


    public static <T extends View<? super T>> T createPageObject(Context context, Class<? extends T> pClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var map = new HashMap<Method, InvocationHandler>();

        Arrays.stream(pClass.getMethods()).forEach(method -> {
            var handler = findHandler(method);
            if (handler != null) {
                map.put(method, handler);
            }
        });
        T obj = createProxyObject(pClass, map);
        obj.prepare(context);
        return obj;
    }


    private static InvocationHandler findHandler(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations())
                .map((a) -> actionHandler.get(a.annotationType()))
                .filter(Objects::nonNull)
                .findAny().orElse(null);
    }

    private static boolean filterAnnotation(Annotation annotation) {
        return actionHandler.get(annotation.annotationType()) != null;
    }


    public static <T> T createProxyObject(Class<T> tClass, Map<Method, ? extends InvocationHandler> map) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        var invocationHandler = new Handler(map);

        return new ByteBuddy()
                .subclass(tClass)
                .method(ElementMatchers.anyOf(map.keySet().toArray(new Method[0])))
                .intercept(InvocationHandlerAdapter.of(invocationHandler))
                .make()
                .load(tClass.getClassLoader())
                .getLoaded()
                .getDeclaredConstructor().newInstance();
    }

    public static class Handler implements InvocationHandler {
        Map<Method, ? extends InvocationHandler> map;

        Handler(Map<Method, ? extends InvocationHandler> map) {
            this.map = map;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return map.get(method).invoke(o, method, objects);
        }
    }
}
