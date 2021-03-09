package com.maciejscislowski.simpledatawarehouse.application.etl;

import java.io.InputStream;

public interface Extractor {

    InputStream extract(String source);

    void start();

    void stop();

}
