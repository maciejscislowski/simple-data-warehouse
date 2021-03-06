package com.maciejscislowski.simpledatawarehouse.application.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.jayway.jsonpath.JsonPath.parse;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class PredefinedQueryBuilder {

    private final ImmutableMap<String, String> templates;

    public PredefinedQueryBuilder(@Value("${resources:classpath:es-queries/*.json}") Resource[] resources) {
        templates = Arrays.stream(resources).collect(toImmutableMap(Resource::getFilename, PredefinedQueryBuilder::asString));
    }

    public String buildQuery(ImmutablePair<String, ImmutableList<ImmutableTriple<String, String, Optional<Object>>>> params) {
        DocumentContext ctx = parse(templates.get(params.getLeft()));
        params.getRight().stream()
                .filter(param -> param.getRight().isPresent())
                .forEach(param -> ctx.put(param.getLeft(), param.getMiddle(), param.getRight().get()));
        return ctx.jsonString();
    }

    static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}