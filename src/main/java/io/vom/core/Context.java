package io.vom.core;

import java.util.Objects;

public class Context {
    protected Driver driver;

    public Driver getDriver() {
        return driver;
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
