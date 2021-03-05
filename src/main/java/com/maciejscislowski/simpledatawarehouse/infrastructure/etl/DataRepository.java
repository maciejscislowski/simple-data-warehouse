package com.maciejscislowski.simpledatawarehouse.infrastructure.etl;

import com.maciejscislowski.simpledatawarehouse.domain.Data;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DataRepository extends ElasticsearchRepository<Data, String> {

}
