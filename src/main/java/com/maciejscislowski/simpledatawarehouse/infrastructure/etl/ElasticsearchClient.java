package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Querier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "es", url = "es:9200")
//@FeignClient(name = "es", url = "https://paas:85e662daa720ca6d4138f135302f117f@oin-us-east-1.searchly.com")
@FeignClient(name = "es", url = "#{'${app.es.host}'}")
public interface ElasticsearchClient extends Querier {

    @GetMapping(value = "/{index}/_search", consumes = MediaType.APPLICATION_JSON_VALUE)
    String search(@PathVariable String index, @RequestBody String query);

    @Override
    default String query(String indexName, String query) {
        return search(indexName, query);
    }

}
