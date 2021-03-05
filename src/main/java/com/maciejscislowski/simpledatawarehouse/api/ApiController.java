package com.maciejscislowski.simpledatawarehouse.api;

import com.google.common.collect.ImmutableList;
import com.maciejscislowski.simpledatawarehouse.application.EtlProcessRunner;
import com.maciejscislowski.simpledatawarehouse.application.PredefinedQueryBuilder;
import com.maciejscislowski.simpledatawarehouse.application.etl.Querier;
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

import static com.maciejscislowski.simpledatawarehouse.application.PredefinedQueryBuilder.*;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.commons.lang3.tuple.ImmutableTriple.of;
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
    @GetMapping(value = "/query", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> query(String query) {
        return supplyAsync(() -> ok(querier.query(indexName, query)));
    }

    @Operation(summary = "Impressions over time (daily)", tags = {"predefined-queries"})
    @GetMapping(value = "/impressions", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> impressions(@RequestParam(required = false) String from,
                                                          @RequestParam(required = false) String size) {
        return supplyAsync(() -> ok(querier.query(indexName,
                queryBuilder.buildQuery(IMPRESSIONS_QUERY, ImmutableList.of(
                        of("$", "from", ofNullable(from)),
                        of("$", "size", ofNullable(size))
                        )
                ))));
    }

    @Operation(summary = "Total Clicks for a given Datasource for a given Date range", tags = {"predefined-queries"})
    @GetMapping(value = "/total-clicks", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> totalClicks(@RequestParam(required = false) String datasource,
                                                          @RequestParam(required = false) String fromDaily,
                                                          @RequestParam(required = false) String toDaily,
                                                          @RequestParam(required = false) String from,
                                                          @RequestParam(required = false) String size) {
        return supplyAsync(() -> ok(querier.query(indexName,
                queryBuilder.buildQuery(TOTAL_CLICKS_QUERY, ImmutableList.of(
                        of("$.query.bool.must[0].term", "datasource.keyword", ofNullable(datasource)),
                        of("$.query.bool.must[0].range.daily", "gte", ofNullable(fromDaily)),
                        of("$.query.bool.must[0].range.daily", "lte", ofNullable(toDaily)),
                        of("$", "from", ofNullable(from)),
                        of("$", "size", ofNullable(size))
                        )
                ))));
    }

    @Operation(summary = "Click-Through Rate (CTR) per Datasource and Campaign", tags = {"predefined-queries"})
    @GetMapping(value = "/ctr", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> ctr(@RequestParam(required = false) String datasource,
                                                  @RequestParam(required = false) String campaign,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String size) {
        return supplyAsync(() -> ok(querier.query(indexName,
                queryBuilder.buildQuery(CTR_QUERY, ImmutableList.of(
                        of("$.query.bool.must[0].term[0]", "datasource.keyword", ofNullable(datasource)),
                        of("$.query.bool.must[0].term[1]", "campaign.keyword", ofNullable(campaign)),
                        of("$", "from", ofNullable(from)),
                        of("$", "size", ofNullable(size))
                        )
                ))));
    }

}
