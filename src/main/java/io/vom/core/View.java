package io.vom.core;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class View<T extends View<T>> implements Searchable {

    private Context context;
    @SuppressWarnings("unchecked")
    private final T _self = (T) this;

    public void prepare(Context context) {
        this.context = context;
    }

    public T job(Consumer<T> consumer) {
        consumer.accept(_self);

        return _self;
    }

    public Context getContext() {
        return context;
    }

    public Locale getLocale() {
        return null;
    }

    @Override
    public Element findElement(Selector selector) {
        return context.getDriver().findElement(selector);
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return context.getDriver().findElements(selector);
    }
}
