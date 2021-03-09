package com.maciejscislowski.simpledatawarehouse.infrastructure.query;

import com.maciejscislowski.simpledatawarehouse.application.query.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
class TemplateQueryBuilder implements QueryBuilder {

    private final TemplateResolver resolver;
    private final TemplateProcessor processor;

    @Override
    public String buildQuery(String queryName, List<ImmutableTriple<String, String, Object>> params) {
        return processor.process(resolver.resolveTemplate(queryName), params);
    }

}
