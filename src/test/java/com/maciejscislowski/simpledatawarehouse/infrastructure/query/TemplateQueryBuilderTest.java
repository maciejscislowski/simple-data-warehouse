package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.jayway.jsonpath.PathNotFoundException;
import com.maciejscislowski.simpledatawarehouse.application.query.Query;
import com.maciejscislowski.simpledatawarehouse.application.query.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jayway.jsonpath.JsonPath.parse;
import static com.maciejscislowski.simpledatawarehouse.application.query.Query.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TemplateQueryBuilderTest {

    private static final Stream<String> PREDEFINED_QUERY_NAMES = Stream.of(
            "ctr.json",
            "impressions.json",
            "total-clicks.json");
    private static final Resource[] PREDEFINED_QUERIES =
            PREDEFINED_QUERY_NAMES.map(fileName -> new ClassPathResource("es-queries/" + fileName))
                    .toArray(Resource[]::new);
    private QueryBuilder builder;

    @BeforeEach
    void setUp() {
        this.builder = new TemplateQueryBuilder(new TemplateResolver(PREDEFINED_QUERIES), new TemplateProcessor());
    }

    @DisplayName("When build the 'ctr' query then all fields are set properly")
    @Test
    void whenBuildCtrQueryThenAllFieldsAreSetProperly() {
        Map<String, String> params = toMap(new String[][]{
                {"datasource", "datasource_value"},
                {"campaign", "campaign_value"},
                {"from", "10"},
                {"size", "2"}
        });

        String query = builder.buildQuery(CTR.getFileName(), CTR.withParams(params));

        assertThat(parse(query).read("$.query.bool.must[0].term['datasource.keyword']").toString()).isEqualTo(params.get("datasource"));
        assertThat(parse(query).read("$.query.bool.must[1].term['campaign.keyword']").toString()).isEqualTo(params.get("campaign"));
        assertThat(parse(query).read("$.from").toString()).isEqualTo(params.get("from"));
        assertThat(parse(query).read("$.size").toString()).isEqualTo(params.get("size"));
    }

    @DisplayName("When build the 'impressions' query then all fields are set properly")
    @Test
    void whenBuildImpressionsQueryThenAllFieldsAreSetProperly() {
        Map<String, String> params = toMap(new String[][]{
                {"from", "10"},
                {"size", "2"}
        });

        String query = builder.buildQuery(IMPRESSIONS.getFileName(), IMPRESSIONS.withParams(params));

        assertThrows(PathNotFoundException.class, () -> parse(query).read("$.query"));
        assertThat(parse(query).read("$.from").toString()).isEqualTo(params.get("from"));
        assertThat(parse(query).read("$.size").toString()).isEqualTo(params.get("size"));
    }

    @DisplayName("When build the 'total-clicks' query then all fields are set properly")
    @Test
    void whenBuildTotalClicksQueryThenAllFieldsAreSetProperly() {
        Map<String, String> params = toMap(new String[][]{
                {"datasource", "datasource_value"},
                {"fromDaily", "from_value"},
                {"toDaily", "to_value"},
                {"from", "10"},
                {"size", "2"}
        });

        String query = builder.buildQuery(TOTAL_CLICKS.getFileName(), TOTAL_CLICKS.withParams(params));

        assertThat(parse(query).read("$.query.bool.must[0].term['datasource.keyword']").toString()).isEqualTo(params.get("datasource"));
        assertThat(parse(query).read("$.query.bool.must[1].range.daily.gte").toString()).isEqualTo(params.get("fromDaily"));
        assertThat(parse(query).read("$.query.bool.must[1].range.daily.lte").toString()).isEqualTo(params.get("toDaily"));
        assertThat(parse(query).read("$.from").toString()).isEqualTo(params.get("from"));
        assertThat(parse(query).read("$.size").toString()).isEqualTo(params.get("size"));
    }

    @DisplayName("When number fields are wrong formatted then throw the NumberFormatException")
    @ParameterizedTest
    @MethodSource("queryParamsWithWrongFormattedFields")
    void whenNumberFieldsAreWrongFormattedThenThrowNumberFormatException(Query query, Map<String, String> params) {
        assertThrows(NumberFormatException.class, () -> builder.buildQuery(query.getFileName(), query.withParams(params)));
    }

    private static Stream<Arguments> queryParamsWithWrongFormattedFields() {
        return Stream.of(
                Arguments.of(CTR, toMap(new String[][]{
                                {"from", "not a number"},
                                {"size", "2"}
                        })),
                Arguments.of(IMPRESSIONS, toMap(new String[][]{
                        {"from", "10"},
                        {"size", "not a number"}
                })),
                Arguments.of(TOTAL_CLICKS, toMap(new String[][]{
                        {"from", "not a number"},
                        {"size", "2"}
                }))
        );
    }

    static Map<String, String> toMap(String[][] array) {
        return Stream.of(array).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

}
