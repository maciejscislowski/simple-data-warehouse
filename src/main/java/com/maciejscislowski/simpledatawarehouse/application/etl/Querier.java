package com.maciejscislowski.simpledatawarehouse.application.etl;

public interface Querier {

    String query(String indexName, String query);

}
