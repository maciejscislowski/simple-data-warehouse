package com.maciejscislowski.simpledatawarehouse.infrastructure.metadata;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends ElasticsearchRepository<Metadata, String> {

}
