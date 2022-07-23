package io.vom.core;

import io.vom.utils.ContextBuilder;
import io.vom.utils.ReflectionUtils;
import io.vom.utils.Selector;
import io.vom.utils.SelectorUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class Context {
    protected Driver driver;

    private Map<String, Selector> commonSelectors;


    public Selector getCommonSelector(String name) {
        if (commonSelectors == null) {
            commonSelectors = SelectorUtils.loadCommonSelectors(this);
        }

        return commonSelectors.get(name);
    }

    public Driver getDriver() {
        return driver;
    }

    public <T extends View<T>> T loadView(@NonNull Class<T> viewClass) {
        try {
            return ReflectionUtils.createPageObject(this, viewClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static SimpleContextBuilder getBuilder() {
        return new SimpleContextBuilder();
    }

    public static class SimpleContextBuilder implements ContextBuilder<SimpleContextBuilder, Context> {

        private final Context context = new Context();

        private Driver driver;

        @Override
        public SimpleContextBuilder setDriver(@NonNull Driver driver) {
            this.driver = driver;

            return this;
        }

        @Override
        public Context build() {
            context.driver = driver;
            driver.prepare(context);

            return context;
        }
    }
}
