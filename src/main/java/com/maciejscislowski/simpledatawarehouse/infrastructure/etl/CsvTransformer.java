package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.application.etl.Transformer;
import com.maciejscislowski.simpledatawarehouse.domain.Data;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static java.util.stream.StreamSupport.stream;

@Component
class CsvTransformer implements Transformer<Stream<Data>> {

    public static final String DATASOURCE_HEADER = "Datasource";
    public static final String CAMPAIGN_HEADER = "Campaign";
    public static final String DAILY_HEADER = "Daily";
    public static final String CLICKS_HEADER = "Clicks";
    public static final String IMPRESSIONS_HEADER = "Impressions";

    @Override
    public Stream<Data> transform(InputStream data) {
        try {
            return stream((CSVFormat.DEFAULT
                    .withHeader(DATASOURCE_HEADER, CAMPAIGN_HEADER, DAILY_HEADER, CLICKS_HEADER, IMPRESSIONS_HEADER)
                    .withFirstRecordAsHeader()
                    .parse(new InputStreamReader(data)).spliterator()), false)
                    .map(record -> Data.builder()
                            .datasource(record.get(DATASOURCE_HEADER))
                            .campaign(record.get(CAMPAIGN_HEADER))
                            .daily(record.get(DAILY_HEADER))
                            .clicks(Long.parseLong(record.get(CLICKS_HEADER)))
                            .impressions(Long.parseLong(record.get(IMPRESSIONS_HEADER)))
                            .build());
        } catch (IOException e) {
            e.printStackTrace();
            return of();
        }
    }

}
