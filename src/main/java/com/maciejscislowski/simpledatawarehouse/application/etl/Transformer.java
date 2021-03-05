package com.maciejscislowski.simpledatawarehouse.application.etl;

import java.io.InputStream;

public interface Transformer<T> {

    T transform(InputStream data);

}
