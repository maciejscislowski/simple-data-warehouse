package com.maciejscislowski.simpledatawarehouse.application;

import com.maciejscislowski.simpledatawarehouse.application.etl.Extractor;
import com.maciejscislowski.simpledatawarehouse.application.etl.Loader;
import com.maciejscislowski.simpledatawarehouse.application.etl.Transformer;
import com.maciejscislowski.simpledatawarehouse.domain.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static java.util.stream.Stream.of;

@RequiredArgsConstructor
@Service
public class EtlProcessRunner {

    private final Extractor extractor;
    private final Transformer<Stream<Data>> transformer;
    private final Loader<Data> loader;

    @Async
    public void runProcess(String source) {
        extractor.start();
        of(source)
                .map(extractor::extract)
                .flatMap(transformer::transform)
                .forEach(loader::load);
        extractor.stop();
    }

}
