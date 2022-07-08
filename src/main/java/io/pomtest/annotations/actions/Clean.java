package io.pomtest.annotations.actions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Action
@Target(ElementType.METHOD)
public @interface Clean {
    String value();

    String path() default "";

    Class<?> returnType() default Void.class;
}
