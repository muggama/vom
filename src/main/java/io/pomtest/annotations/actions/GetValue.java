package io.pomtest.annotations.actions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetValue {
    String value();

    String path() default "";

    Class<?> returnType() default Void.class;
}
