package io.vom.utils;

import com.beust.jcommander.internal.Lists;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public interface Collectable<E extends ElementSupplier> {

    default Duration getScrollDuration() {
        return Properties.DEFAULT_SCROLL_DURATION;
    }

    default int getScrollLength() {
        return Properties.DEFAULT_SCROLL_LENGTH;
    }

    default ScrollDirection getScrollDirection() {
        return ScrollDirection.DOWN;
    }

    default E getAnyItem() {
        return CollectionUtils.getAny(getViewList());
    }


    default List<E> collect() {
        var l = Lists.<E>newArrayList();
        List<E> before = getViewList();
        List<E> after;

        while (true) {
            VomUtils.scroll(before.stream().map(ElementSupplier::getElement).collect(Collectors.toList())
                    , getScrollDirection()
                    , getScrollDuration()
                    , getScrollLength());
            l.addAll(before);
            after = getViewList();

            if (before.equals(after)) {
                break;
            } else {
                before = after;
            }
        }

        return l.stream().distinct().collect(Collectors.toList());
    }

    List<E> getViewList();
}
