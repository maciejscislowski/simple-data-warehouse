package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.google.common.collect.ImmutableList;
import com.maciejscislowski.simpledatawarehouse.application.etl.Transformer;
import com.maciejscislowski.simpledatawarehouse.domain.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvTransformerTest {

    private Transformer<Stream<Data>> transformer;
    private InputStream testDataStream;

    @BeforeEach
    void setUp() throws IOException {
        this.transformer = new CsvTransformer();
        this.testDataStream = new ClassPathResource("test-data.csv").getInputStream();
    }

    @DisplayName("When transforming data then all data are transformed correctly")
    @ParameterizedTest
    @MethodSource("testData")
    void whenTransformingDataThenAllDataAreTransformedCorrectly(List<Data> testData) {
        Stream<Data> data = transformer.transform(testDataStream);

        assertThat(data).containsAll(testData)
                .doesNotContain(Data.builder().datasource("not existing datasource").build());
    }

    private static Stream<List<Data>> testData() {
        return Stream.of(
                ImmutableList.of(
                        Data.builder()
                                .datasource("Google Ads")
                                .campaign("Adventmarkt Touristik")
                                .daily("11/12/19")
                                .clicks(7L)
                                .impressions(22_425L)
                                .build(),
                        Data.builder()
                                .datasource("Twitter Ads")
                                .campaign("Motorrad Mitgliedschaft")
                                .daily("04/30/19")
                                .clicks(13L)
                                .impressions(387L)
                                .build(),
                        Data.builder()
                                .datasource("Facebook Ads")
                                .campaign("Schutzbrief")
                                .daily("04/18/19")
                                .clicks(0L)
                                .impressions(1L)
                                .build()
                ));
    }

}
