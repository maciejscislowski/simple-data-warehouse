package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Querier;
import com.maciejscislowski.simpledatawarehouse.infrastructure.UrlCredentialsUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.join;

@Component
class ElasticsearchClient implements Querier {

    private final RestTemplate restTemplate;
    private final String url;
    private final byte[] auth;

    public ElasticsearchClient(@Value("${app.es.host}") String host,
                               @Value("${app.es.data-index-name}") String index,
                               RestTemplate restTemplate) {
        auth = Base64.encodeBase64(UrlCredentialsUtil.parseUrl(host).v1().getBytes());
        url = join("/", UrlCredentialsUtil.parseUrl(host).v2(), index, "_search");
        this.restTemplate = restTemplate;
    }

    @Override
    public String query(String indexName, String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(new String(auth));
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
    }

}
