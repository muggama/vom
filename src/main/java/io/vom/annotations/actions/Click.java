package io.vom.annotations.actions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Click {
    String value();

    String path() default "";

    Class<?> returnType() default Void.class;
}
