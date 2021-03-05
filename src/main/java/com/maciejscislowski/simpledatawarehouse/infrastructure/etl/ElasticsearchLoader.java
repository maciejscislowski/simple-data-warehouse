package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Loader;
import com.maciejscislowski.simpledatawarehouse.domain.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class ElasticsearchLoader implements Loader<Data> {

    private final DataRepository dataRepository;

    @Override
    public void load(Data data) {
        dataRepository.save(data);
    }

}
