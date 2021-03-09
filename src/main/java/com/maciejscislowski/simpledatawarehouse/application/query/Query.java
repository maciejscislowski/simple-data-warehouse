package com.maciejscislowski.simpledatawarehouse.application.query;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.tuple.ImmutableTriple.of;

public enum Query {

    CTR("ctr.json") {
        @Override
        public List<ImmutableTriple<String, String, Object>> withParams(Map<String, String> params) {
            return ImmutableList.copyOf(Stream.of(
                    field("term", "datasource.keyword", "datasource", params, text),
                    field("term", "campaign.keyword", "campaign", params, text),
                    field("$", "from", "from", params, number),
                    field("$", "size", "size", params, number))
                    .filter(Objects::nonNull).collect(Collectors.toList()));
        }
    },
    IMPRESSIONS("impressions.json") {
        @Override
        public List<ImmutableTriple<String, String, Object>> withParams(Map<String, String> params) {
            return ImmutableList.copyOf(Stream.of(
                    field("$", "from", "from", params, number),
                    field("$", "size", "size", params, number))
                    .filter(Objects::nonNull).collect(Collectors.toList()));
        }
    },
    TOTAL_CLICKS("total-clicks.json") {
        @Override
        public List<ImmutableTriple<String, String, Object>> withParams(Map<String, String> params) {
            return ImmutableList.copyOf(Stream.of(
                    field("term", "datasource.keyword", "datasource", params, text),
                    field("range.daily", "gte", "fromDaily", params, text),
                    field("range.daily", "lte", "toDaily", params, text),
                    field("$", "from", "from", params, number),
                    field("$", "size", "size", params, number))
                    .filter(Objects::nonNull).collect(Collectors.toList()));
        }
    };

    ImmutableTriple<String, String, Object> field(String path, String node, String paramName, Map<String, String> params,
                                                  BiFunction<String, Map<String, String>, String> calculation) {
        return StringUtils.hasText(params.get(paramName)) ? of(path, node, calculation.apply(paramName, params)) : null;
    }

    @Getter
    final String fileName;
    final BiFunction<String, Map<String, String>, String> text = (paramName, params) -> params.get(paramName);
    final BiFunction<String, Map<String, String>, String> number = (paramName, params) -> String.valueOf(parseInt(params.get(paramName)));

    Query(String fileName) {
        this.fileName = fileName;
    }

    public abstract List<ImmutableTriple<String, String, Object>> withParams(Map<String, String> params);

}
