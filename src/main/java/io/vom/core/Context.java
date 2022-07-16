package io.vom.core;

import io.vom.utils.ReflectionUtils;
import io.vom.utils.Selector;
import io.vom.utils.SelectorUtils;

import java.util.Map;
import java.util.Objects;

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

    public <T extends View<T>> T loadView(Class<T> viewClass) {
        try {
            return ReflectionUtils.createPageObject(this, viewClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static ContextBuilder getBuilder() {
        return new ContextBuilder();
    }

    public static class ContextBuilder {

        private final Context context = new Context();

        private Driver driver;

        public ContextBuilder setDriver(Driver driver) {
            this.driver = driver;

            return this;
        }

        public Context build() {
            Objects.requireNonNull(driver, "Driver is null, you should set Driver to start project");
            context.driver = driver;

            return context;
        }
    }
}
