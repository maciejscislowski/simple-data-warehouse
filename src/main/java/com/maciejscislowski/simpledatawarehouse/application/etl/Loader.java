package com.maciejscislowski.simpledatawarehouse.application.etl;

public interface Loader<T> {

    void load(T data);

}
