package com.maciejscislowski.simpledatawarehouse.application.query;

public interface Querier {

    String queryForPage(String indexName, String query);

}
