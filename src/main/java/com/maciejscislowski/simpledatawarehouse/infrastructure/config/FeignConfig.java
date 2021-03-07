package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("heroku")
@Configuration
public class FeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(ElasticsearchProperties properties) {
        return new BasicAuthRequestInterceptor(properties.getUser(), properties.getPassword());
    }

}
