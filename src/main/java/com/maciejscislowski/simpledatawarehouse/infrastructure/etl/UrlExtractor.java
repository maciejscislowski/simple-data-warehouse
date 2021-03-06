package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Extractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.NullInputStream;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
class UrlExtractor implements Extractor {

    public static final String TEXT_CSV_CHARSET_UTF_8_VALUE = "text/csv;charset=utf-8";
    private final RestTemplate restTemplate;

    @CacheEvict(value = "data", allEntries = true)
    @Override
    public InputStream extract(final String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, TEXT_CSV_CHARSET_UTF_8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        log.info("Extracted file {} length", response.getHeaders().getContentLength());

        return nonNull(response.getBody()) ? new ByteArrayInputStream(response.getBody()) : new NullInputStream(0);
    }

}
