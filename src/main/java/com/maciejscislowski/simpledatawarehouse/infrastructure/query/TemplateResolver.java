package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
class TemplateResolver {

    private final ImmutableMap<String, String> templates;

    public TemplateResolver(@Value("${resources:classpath:es-queries/*.json}") Resource[] resources) {
        templates = Arrays.stream(resources).collect(toImmutableMap(Resource::getFilename, TemplateResolver::asString));
    }

    public String resolveTemplate(String name) {
        return templates.get(name);
    }

    static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
