package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
class WebConfig implements WebMvcConfigurer {

    @Value("${app.es.host}")
    private String host;

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        URI uri = null;
        try {
            uri = new URI(host);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }

        final String[] userInfo = uri.getUserInfo().split(":");
        return new BasicAuthRequestInterceptor(userInfo[0], userInfo[1]);
    }

}
