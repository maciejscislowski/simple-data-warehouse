package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component("elasticsearchCachedQuerier")
class ElasticsearchCachedQuerier implements Querier {

    private final ElasticsearchQuerier elasticsearchQuerier;

    @Cacheable("data")
    @Override
    public String query(String indexName, String query) {
        log.info("Querying from {} index", indexName);
        return elasticsearchQuerier.query(indexName, query);
    }

}
