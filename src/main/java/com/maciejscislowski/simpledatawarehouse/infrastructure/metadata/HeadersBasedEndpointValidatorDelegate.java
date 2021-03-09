package com.maciejscislowski.simpledatawarehouse.infrastructure.metadata;

import com.maciejscislowski.simpledatawarehouse.api.validators.EndpointValidatorDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class HeadersBasedEndpointValidatorDelegate implements EndpointValidatorDelegate {

    public static final String TEXT_CSV_VALUE = "text/csv";
    private final RestTemplate restTemplate;
    private final MetadataRepository repository;

    @Override
    public List<String> validateEndpoint(String endpoint) {
        List<String> errors = newArrayList();
        HttpHeaders headers;
        try {
            headers = restTemplate.headForHeaders(endpoint);
        } catch (Exception e) {
            String msg = format("Cannot call %s. %s", endpoint, e.getMessage());
            errors.add(msg);
            log.error(msg);
            e.printStackTrace();
            return errors;
        }

        if (isNull(headers.getContentType()) || !TEXT_CSV_VALUE.equals(headers.getContentType().toString())) {
            errors.add(format("Content-Type %s is not supported, must be %s", headers.getContentType(), TEXT_CSV_VALUE));
        }
        if (!(headers.getContentLength() > 0)) {
            errors.add("Content-Length cannot be 0");
        }
        boolean etlProcessRunning = repository.findAll(PageRequest.of(0, 1))
                .get().findFirst().map(Metadata::isEtlProcessRunning).orElse(false);
        if (etlProcessRunning) {
            errors.add("Extract process has already been started");
        }
        return errors;
    }
}
