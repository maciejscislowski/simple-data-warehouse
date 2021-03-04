package com.maciejscislowski.simpledatawarehouse.infrastructure.a;

import com.maciejscislowski.simpledatawarehouse.application.Uploader;
import com.maciejscislowski.simpledatawarehouse.domain.Data;
import com.maciejscislowski.simpledatawarehouse.infrastructure.repositories.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader implements Uploader {

    public static final String DATE_FORMAT = "MM/dd/yy";
    public static final String TEXT_CSV_CHARSET_UTF_8_VALUE = "text/csv;charset=utf-8";
    public static final String[] HEADER_NAMES = {"Datasource", "Campaign", "Daily", "Clicks", "Impressions"};
    private final RestTemplate restTemplate;
    private final DataRepository dataRepository;
    private final AtomicLong rows = new AtomicLong(0);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Override
    public String uploadFromUrl(final String url) {
        rows.set(0);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, TEXT_CSV_CHARSET_UTF_8_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        try {
            process(new ByteArrayInputStream(response.getBody()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Processed {} rows", rows.get());
        return "";
    }

    private String process(InputStream part) throws IOException {
        CSVFormat.DEFAULT
                .withHeader(HEADER_NAMES)
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(part))
                .forEach(this::processRecord);
        return "";
    }

    private void processRecord(CSVRecord record) {
        rows.incrementAndGet();
        dataRepository.save(Data.builder()
                .datasource(record.get("Datasource"))
                .campaign(record.get("Campaign"))
//                .daily(LocalDate.parse(record.get("Daily"), DATE_FORMATTER))
                .daily(record.get("Daily"))
                .clicks(Long.parseLong(record.get("Clicks")))
                .impressions(Long.parseLong(record.get("Impressions")))
                .build());
    }

}
