package com.maciejscislowski.simpledatawarehouse.application.query;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.List;

public interface QueryBuilder {

    String buildQuery(String queryName, List<ImmutableTriple<String, String, Object>> params);

}
