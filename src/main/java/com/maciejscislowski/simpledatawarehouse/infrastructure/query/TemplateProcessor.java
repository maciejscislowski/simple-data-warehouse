package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;
import static com.jayway.jsonpath.JsonPath.parse;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Component
public class TemplateProcessor {

    public String process(String template, List<ImmutableTriple<String, String, Object>> params) {
        final DocumentContext ctx = parse(template);
        if (params.isEmpty()) {
            return ctx.jsonString();
        }
        addRootParams(params, ctx);
        boolean queryFieldsExists = params.stream().map(ImmutableTriple::getLeft).anyMatch(field -> !"$".equals(field));
        if (!queryFieldsExists) {
            return ctx.jsonString();
        }
        ctx.put("$", "query", newHashMap())
                .put("$.query", "bool", newHashMap())
                .put("$.query.bool", "must", new JSONArray());
        addTermParams(params, ctx);
        addRangeParams(params, ctx);
        return ctx.jsonString();
    }

    private void addRangeParams(List<ImmutableTriple<String, String, Object>> params, DocumentContext ctx) {
        boolean rangeFieldsExists = params.stream().map(ImmutableTriple::getLeft).anyMatch(field -> field.startsWith("range"));
        if (!rangeFieldsExists) {
            return;
        }
        ctx.add("$.query.bool.must", newHashMap());
        int index = ((JSONArray) ctx.read("$.query.bool.must")).size() - 1;
        ctx.put(format("$.query.bool.must[%d]", index), "range", newHashMap())
                .put(format("$.query.bool.must[%d].range", index), "daily", newHashMap());
        params.stream()
                .filter(param -> requireNonNull(param.getLeft()).startsWith("range"))
                .forEach(param -> ctx.put(format("$.query.bool.must[%d].range.daily", index), param.getMiddle(), param.getRight()));
    }

    private void addTermParams(List<ImmutableTriple<String, String, Object>> params, DocumentContext ctx) {
        IntStream.range(0, (int) params.stream().filter(param -> "term".equals(param.getLeft())).count())
                .filter(index -> "term".equals(params.get(index).getLeft()))
                .forEach(index -> ctx
                        .add("$.query.bool.must", newHashMap())
                        .put(format("$.query.bool.must[%d]", index), "term", newHashMap())
                        .put(format("$.query.bool.must[%d].term", index), params.get(index).getMiddle(), params.get(index).getRight()));
    }

    private void addRootParams(List<ImmutableTriple<String, String, Object>> params, DocumentContext ctx) {
        params.stream()
                .filter(param -> "$".equals(param.getLeft()))
                .forEach(param -> ctx.put("$", param.getMiddle(), param.getRight()));
    }

}
