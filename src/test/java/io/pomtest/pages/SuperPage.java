package io.pomtest.pages;

import io.pomtest.core.Page;

public class SuperPage<T extends SuperPage<T>> extends Page<T> {
}
