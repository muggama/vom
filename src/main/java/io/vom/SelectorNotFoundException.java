package io.vom;

public class SelectorNotFoundException extends RuntimeException{
    public SelectorNotFoundException(String cause){
        super(cause);
    }
}
