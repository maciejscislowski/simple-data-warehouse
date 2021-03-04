package com.maciejscislowski.simpledatawarehouse.infrastructure.repositories;

import com.maciejscislowski.simpledatawarehouse.domain.Data;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends ElasticsearchRepository<Data, String> {

//    List

}
