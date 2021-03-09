package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("cachedQuerier")
class ElasticsearchCachedQuerier implements Querier {

    private final ElasticsearchQuerier elasticsearchQuerier;

    @Cacheable("data")
    @Override
    public String query(String indexName, String query) {
        try {
            return elasticsearchQuerier.query(indexName, query);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
