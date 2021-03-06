package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Extractor;
import com.maciejscislowski.simpledatawarehouse.infrastructure.metadata.Metadata;
import com.maciejscislowski.simpledatawarehouse.infrastructure.metadata.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.NullInputStream;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
class UrlExtractor implements Extractor {

    public static final String TEXT_CSV_CHARSET_UTF_8_VALUE = "text/csv;charset=utf-8";
    private final RestTemplate restTemplate;
    private final MetadataRepository metadataRepository;
    private final DataRepository dataRepository;

    @Override
    public InputStream extract(final String url) {
        log.info("Cache was evicted");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, TEXT_CSV_CHARSET_UTF_8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        log.info("Extracted file {} length", response.getHeaders().getContentLength());

        dataRepository.deleteAll();
        log.info("Data index was cleared");

        return nonNull(response.getBody()) ? new ByteArrayInputStream(response.getBody()) : new NullInputStream(0);
    }

    @CacheEvict(value = "data", allEntries = true)
    @Override
    public void start() {
        log.info("Process has been started");
        metadataRepository.save(
                metadataRepository.findAll(PageRequest.of(0, 1))
                        .get().findFirst().orElse(Metadata.builder()
                        .etlProcessLastStarted(now())
                        .build())
                        .updateEtlProcessRunning(true)
                        .updateEtlProcessLastStarted(now()));
        log.info("Metadata has been updated");
    }

    @CacheEvict(value = "data", allEntries = true)
    @Override
    public void stop() {
        log.info("Process has been stopped");
        metadataRepository.save(
                metadataRepository.findAll(PageRequest.of(0, 1))
                        .get().findFirst().orElseThrow(() -> new RuntimeException("Metadata not found"))
                        .updateEtlProcessRunning(false));
        log.info("Metadata has been updated");
    }

    static LocalDateTime now() {
        return LocalDateTime.now();
    }

}
