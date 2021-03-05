package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@EnableElasticsearchRepositories
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${app.es.host}")
    private String host;

    @Value("${app.es.data-index-name}")
    private String dataIndexName;

    @Bean
    public RestClientBuilder restClientBuilder() {
        URI uri = null;
        try {
            uri = new URI(host);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }

        final String[] userInfo = uri.getUserInfo().split(":");
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userInfo[0], userInfo[1]));

        return RestClient.builder(
                        new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider));
    }

    @Override
    public RestHighLevelClient elasticsearchClient() {
        return new RestHighLevelClient(restClientBuilder());
    }

    @Bean
    public String dataIndexName() {
        return dataIndexName;
    }

}
