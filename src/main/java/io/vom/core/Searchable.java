package io.vom.core;

import io.vom.utils.Selector;

import java.util.List;

public interface Searchable {

    Element findElement(Selector selector);

    Element findNullableElement(Selector selector);

    List<Element> findElements(Selector selector);

}
