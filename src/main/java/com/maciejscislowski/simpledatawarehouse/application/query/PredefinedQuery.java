package com.maciejscislowski.simpledatawarehouse.application.query;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.tuple.ImmutableTriple.of;

public enum PredefinedQuery {

    CTR("ctr.json") {
        @Override
        public ImmutablePair<String, ImmutableList<ImmutableTriple<String, String, Optional<Object>>>> withParams(Map<String, Optional<Object>> params) {
            return ImmutablePair.of(this.fileName, ImmutableList.of(
                    of("$.query.bool.must[0].term", "datasource.keyword", params.get("datasource")),
                    of("$.query.bool.must[1].term", "campaign.keyword", params.get("campaign")),
                    of("$", "from", params.get("from")),
                    of("$", "size", params.get("size")))
            );
        }
    },
    IMPRESSIONS("impressions.json") {
        @Override
        public ImmutablePair<String, ImmutableList<ImmutableTriple<String, String, Optional<Object>>>> withParams(Map<String, Optional<Object>> params) {
            return ImmutablePair.of(this.fileName, ImmutableList.of(
                    of("$", "from", params.get("from")),
                    of("$", "size", params.get("size")))
            );
        }
    },
    TOTAL_CLICKS("total-clicks.json") {
        @Override
        public ImmutablePair<String, ImmutableList<ImmutableTriple<String, String, Optional<Object>>>> withParams(Map<String, Optional<Object>> params) {
            return ImmutablePair.of(this.fileName, ImmutableList.of(
                    of("$.query.bool.must[0].term", "datasource.keyword", params.get("datasource")),
                    of("$.query.bool.must[1].range.daily", "gte", params.get("fromDaily")),
                    of("$.query.bool.must[1].range.daily", "lte", params.get("toDaily")),
                    of("$", "from", params.get("from")),
                    of("$", "size", params.get("size")))
            );
        }
    };

    final String fileName;

    PredefinedQuery(String fileName) {
        this.fileName = fileName;
    }

    public abstract ImmutablePair<String, ImmutableList<ImmutableTriple<String, String, Optional<Object>>>> withParams(Map<String, Optional<Object>> params);

}
