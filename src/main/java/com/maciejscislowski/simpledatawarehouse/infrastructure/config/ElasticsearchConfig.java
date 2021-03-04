package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${app.es.host}")
    private String host;

    @Value("${app.es.data-index-name}")
    private String dataIndexName;

    @Override
    public RestHighLevelClient elasticsearchClient() {
//        return new RestHighLevelClient((RestClient.builder(new HttpHost(host))));
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public String dataIndexName() {
        return dataIndexName;
    }

}
