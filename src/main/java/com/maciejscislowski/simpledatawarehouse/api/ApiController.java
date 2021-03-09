package com.maciejscislowski.simpledatawarehouse.api;

import com.maciejscislowski.simpledatawarehouse.api.validators.CsvEndpoint;
import com.maciejscislowski.simpledatawarehouse.api.validators.EndpointValidatorGroup;
import com.maciejscislowski.simpledatawarehouse.api.validators.EndpointValidatorSequence;
import com.maciejscislowski.simpledatawarehouse.application.EtlProcessRunner;
import com.maciejscislowski.simpledatawarehouse.application.query.Querier;
import com.maciejscislowski.simpledatawarehouse.application.query.QueryBuilder;
import com.maciejscislowski.simpledatawarehouse.infrastructure.config.ElasticsearchProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.maciejscislowski.simpledatawarehouse.application.query.Query.*;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.commons.lang3.StringUtils.chomp;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Validated(EndpointValidatorSequence.class)
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
class ApiController {

    @Qualifier("cachedQuerier")
    private final Querier querier;
    private final ElasticsearchProperties properties;
    private final QueryBuilder queryBuilder;
    private final EtlProcessRunner etlProcessRunner;

    @Operation(summary = "Extract data from a csv file", tags = {"extract"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/extract")
    void extract(
            @URL(groups = Default.class)
            @CsvEndpoint(groups = EndpointValidatorGroup.class)
            @RequestParam String url) {
        log.info("Extracting data from URL {}", url);
        etlProcessRunner.runProcess(url);
    }

    @Operation(summary = "Search with the Elasticsearch query", tags = {"query"})
    @PostMapping(value = "/query", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> query(@RequestBody(required = false) String query) {
        return supplyAsync(() -> ok(
                querier.query(properties.getDataIndexName(), chomp(query))));
    }

    @Operation(summary = "Impressions over time (daily)", tags = {"predefined-queries"},
            parameters = {
                    @Parameter(name = "from"),
                    @Parameter(name = "size"),
            })
    @GetMapping(value = "/impressions", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> impressions(@RequestParam Map<String, String> params) {
        return supplyAsync(() -> ok(querier.query(properties.getDataIndexName(), queryBuilder.buildQuery(
                IMPRESSIONS.getFileName(), IMPRESSIONS.withParams(params)))));
    }

    @Operation(summary = "Total Clicks for a given Datasource for a given Date range", tags = {"predefined-queries"},
            parameters = {
                    @Parameter(name = "datasource"),
                    @Parameter(name = "fromDaily"),
                    @Parameter(name = "toDaily"),
                    @Parameter(name = "from"),
                    @Parameter(name = "size"),
            })
    @GetMapping(value = "/total-clicks", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> totalClicks(@RequestParam Map<String, String> params) {
        return supplyAsync(() -> ok(querier.query(properties.getDataIndexName(), queryBuilder.buildQuery(
                TOTAL_CLICKS.getFileName(), TOTAL_CLICKS.withParams(params)))));
    }

    @Operation(summary = "Click-Through Rate (CTR) per Datasource and Campaign", tags = {"predefined-queries"},
            parameters = {
                    @Parameter(name = "datasource"),
                    @Parameter(name = "campaign"),
                    @Parameter(name = "from"),
                    @Parameter(name = "size"),
            })
    @GetMapping(value = "/ctr", produces = APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<String>> ctr(@RequestParam Map<String, String> params) {
        return supplyAsync(() -> ok(querier.query(properties.getDataIndexName(), queryBuilder.buildQuery(
                CTR.getFileName(), CTR.withParams(params)))));
    }

}
