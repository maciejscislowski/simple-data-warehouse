package com.maciejscislowski.simpledatawarehouse.api;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.maciejscislowski.simpledatawarehouse.application.Uploader;
import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
class ApiController {

    private final Uploader uploader;
    private final Querier querier;
    @Qualifier("dataIndexName")
    private final String indexName;
    @Value("classpath:es-queries/ctr.json")
    private Resource ctrQuery;

    @PostMapping("/upload")
    CompletableFuture<String> uploadFromUrl(@URL(host = "adverity-challenge.s3-website-eu-west-1.amazonaws.com") @RequestParam String url) {
        log.info("Uploading from URL {}", url);
        return CompletableFuture.supplyAsync(() -> uploader.uploadFromUrl(url));
    }

    @GetMapping("/search")
    CompletableFuture<ResponseEntity<String>> queryForPage(String query) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(querier.queryForPage(indexName, query)));
    }

    @GetMapping("/total-clicks")
    CompletableFuture<ResponseEntity<String>> totalClicks(String query) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(querier.queryForPage(indexName, asString(ctrQuery)))); //predefiend query
    }

    @GetMapping("/ctr")
    CompletableFuture<ResponseEntity<String>> ctr(@RequestParam String from, @RequestParam String to) {
        DocumentContext parsedDataContext = JsonPath.parse(asString(ctrQuery));
        parsedDataContext.add("$.query.bool.must[1].daily.gte", "from");
        parsedDataContext.add("$.query.bool.must[1].daily.lte", "to");

        System.out.println(parsedDataContext.jsonString());

        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(querier.queryForPage(indexName, parsedDataContext.jsonString()))); //predefiend query
    }

    @GetMapping("/impressions-over-time")
    CompletableFuture<ResponseEntity<String>> impressionsOverTime(String query) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(querier.queryForPage(indexName, asString(ctrQuery)))); //predefiend query
    }

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
