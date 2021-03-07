package com.maciejscislowski.simpledatawarehouse.application.query;

public interface Querier {

    String query(String indexName, String query);

}
