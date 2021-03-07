package com.maciejscislowski.simpledatawarehouse.api;

import com.google.common.collect.ImmutableMap;
import com.maciejscislowski.simpledatawarehouse.application.EtlProcessRunner;
import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import com.maciejscislowski.simpledatawarehouse.application.query.PredefinedQueryBuilder;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.maciejscislowski.simpledatawarehouse.application.query.PredefinedQuery.*;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
class ApiController {

    @Qualifier("dataIndexName")
    private final String indexName;
    private final EtlProcessRunner etlProcessRunner;
    private final Querier querier;
    private final PredefinedQueryBuilder queryBuilder;

    @Operation(summary = "Extract data from a csv file", tags = {"extract"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/extract")
    void extract(@URL @RequestParam String url) {
        log.info("Extracting data from URL {}", url);
        etlProcessRunner.runProcess(url);
    }

    @Operation(summary = "Search with the Elasticsearch query", tags = {"query"})
    @PostMapping(value = "/query", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> query(@RequestBody String query) {
        return supplyAsync(() -> ok(querier.query(indexName, query)));
    }

    @Operation(summary = "Impressions over time (daily)", tags = {"predefined-queries"})
    @GetMapping(value = "/impressions", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> impressions(@RequestParam(required = false) Long from,
                                                          @RequestParam(required = false) Long size) {
        return supplyAsync(() -> ok(querier.query(indexName, queryBuilder.buildQuery(
                IMPRESSIONS.withParams(ImmutableMap.of(
                        "from", ofNullable(from),
                        "size", ofNullable(size)))
        ))));
    }

    @Operation(summary = "Total Clicks for a given Datasource for a given Date range", tags = {"predefined-queries"})
    @GetMapping(value = "/total-clicks", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> totalClicks(@RequestParam String datasource,
                                                          @RequestParam String fromDaily,
                                                          @RequestParam String toDaily,
                                                          @RequestParam(required = false) Long from,
                                                          @RequestParam(required = false) Long size) {
        return supplyAsync(() -> ok(querier.query(indexName, queryBuilder.buildQuery(
                TOTAL_CLICKS.withParams(ImmutableMap.of(
                        "datasource", of(datasource),
                        "fromDaily", of(fromDaily),
                        "toDaily", of(toDaily),
                        "from", ofNullable(from),
                        "size", ofNullable(size)))
        ))));
    }

    @Operation(summary = "Click-Through Rate (CTR) per Datasource and Campaign", tags = {"predefined-queries"})
    @GetMapping(value = "/ctr", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> ctr(@RequestParam String datasource,
                                                  @RequestParam String campaign,
                                                  @RequestParam(required = false) Long from,
                                                  @RequestParam(required = false) Long size) {
        return supplyAsync(() -> ok(querier.query(indexName, queryBuilder.buildQuery(
                CTR.withParams(ImmutableMap.of(
                        "datasource", of(datasource),
                        "campaign", of(campaign),
                        "from", ofNullable(from),
                        "size", ofNullable(size)))
        ))));
    }

}
