package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.util.StringUtils.hasText;

@Qualifier("querier")
@FeignClient(name = "es", url = "#{@elasticsearchProperties.getFullHost()}")
public interface ElasticsearchQuerier extends Querier {

    @PostMapping(value = "/{index}/_search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String search(@PathVariable String index, String query);

    @PostMapping(value = "/{index}/_search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String search(@PathVariable String index);

    default String query(String index, String query) {
        return hasText(query) ? search(index, query) : search(index);
    }

}
