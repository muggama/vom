package io.vom.views;

import io.vom.core.Element;
import io.vom.core.View;
import io.vom.utils.Collectable;
import io.vom.utils.ElementSupplier;
import io.vom.utils.Selector;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestView extends View<TestView> implements Collectable<TestView.Fruit> {


    Selector lists;

    @Override
    public List<Fruit> getViewList() {
        return findElements(lists)
                .stream()
                .map(Fruit::new)
                .collect(Collectors.toList());
    }

    public static class Fruit implements ElementSupplier {

        Element element;

        private final String name;

        Fruit(Element element) {
            this.element = element;
            this.name = element.getText();
        }

        @Override
        public Element getElement() {
            return element;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Fruit fruit = (Fruit) o;

            return Objects.equals(name, fruit.name);
        }


        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Fruit{" +
                    "fruit=" + getName() +
                    '}';
        }
    }

}
