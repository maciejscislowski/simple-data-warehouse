package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Querier;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@Component
class ElasticsearchClient implements Querier {

    @Value("${app.es.host}")
    private String host;

    @Value("${app.es.data-index-name}")
    private String dataIndexName;

    private final RestTemplate restTemplate;

    @Override
    public String query(String indexName, String query) {
        URI uri = null;
        try {
            uri = new URIBuilder().setScheme(host.split(":")[0])
                    .setHost(host.split("://")[1]).setPathSegments(dataIndexName, "_search").build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.getForObject(uri, String.class);
    }

}
