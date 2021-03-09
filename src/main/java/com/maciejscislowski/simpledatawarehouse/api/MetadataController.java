package com.maciejscislowski.simpledatawarehouse.api;

import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import com.maciejscislowski.simpledatawarehouse.infrastructure.config.ElasticsearchProperties;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/metadata")
@RestController
public class MetadataController {

    @Qualifier("querier")
    private final Querier querier;
    private final ElasticsearchProperties properties;

    @Operation(summary = "Returns metadata about kept data and the ETL process", tags = {"query"})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> metadata() {
        return supplyAsync(() -> ok(
                querier.query(properties.getMetadataIndexName(), "{}")));
    }

}
