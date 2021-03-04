package com.maciejscislowski.simpledatawarehouse.infrastructure.a;

import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "es", url = "http://127.0.0.1:9200")
public interface ElasticsearchClient extends Querier {

    @GetMapping(value = "/{index}/_search", consumes = MediaType.APPLICATION_JSON_VALUE)
    String search(@PathVariable String index, @RequestBody String query);

    @Override
    default String queryForPage(String indexName, String query) {
        return search(indexName, query);
    }

}
