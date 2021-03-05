package com.maciejscislowski.simpledatawarehouse.infrastructure;

import org.elasticsearch.common.collect.Tuple;

import static org.elasticsearch.common.collect.Tuple.tuple;

public class UrlCredentialsUtil {

    public static Tuple<String, String> parseUrl(String url) {
        String credentials = url.split("//")[1].split("@")[0];
        String clearUrl = String.join("//", url.split("//")[0], url.split("@")[1]);
        return tuple(credentials, clearUrl);
    }

}
