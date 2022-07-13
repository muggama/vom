package io.vom.utils;

import io.vom.annotations.actions.Clear;
import io.vom.annotations.actions.Click;
import io.vom.annotations.actions.GetValue;
import io.vom.annotations.actions.SetValue;
import io.vom.core.Context;
import io.vom.core.Repository;
import io.vom.core.Selector;
import io.vom.core.View;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Reflection {

    private static final Map<Class<? extends Annotation>, InvocationHandler> actionHandler = new HashMap<>();

    static {
        actionHandler.put(GetValue.class, Reflection::invokeGetter);
        actionHandler.put(SetValue.class, Reflection::invokeSetter);
        actionHandler.put(Clear.class, Reflection::invokeClearer);
        actionHandler.put(Click.class, Reflection::invokeClicker);
    }


    public static <T extends View<T>> T createPageObject(Context context, Class<T> pClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
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

    private static Object invokeClearer(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SetValue annotation = method.getDeclaredAnnotation(SetValue.class);
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        view.findElement(selector).clear();

        return getReturn(self, method, annotation);
    }

    private static Object invokeSetter(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SetValue annotation = method.getDeclaredAnnotation(SetValue.class);
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);

        if (selector == null)
            throw new IllegalStateException("Method: with name '" + method.getName() + "' is not declared on repository");

        var text = Objects.requireNonNull(objects[0], method.getName() + "argument is null").toString();
        view.findElement(selector).setText(text);

        return getReturn(self, method, annotation);
    }

    private static Object getReturn(Object self, Method method, SetValue annotation) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
                    throw new ClassCastException("Method: '" + method.getName() + "'s return type is different than expected ");
                }
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes", "SuspiciousInvocationHandlerImplementation"})
    private static Object invokeClicker(Object self, Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        view.findElement(selector).click();
        Class<? extends View> returnClass = (Class<? extends View>) method.getReturnType();

        return createPageObject(view.getContext(),returnClass);
    }

    private static Object invokeGetter(Object self, Method method, Object[] objects) {
        var view = (View<?>) self;
        Selector selector = Repository.findSelector(view.getContext(), view, method);
        if (method.getReturnType().isAssignableFrom(String.class)) {
            //noinspection SuspiciousInvocationHandlerImplementation
            return view.findElement(selector).getText();
        } else {
            throw new ClassCastException("Method: " + method.getName() + "'s return type must be String");
        }
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
