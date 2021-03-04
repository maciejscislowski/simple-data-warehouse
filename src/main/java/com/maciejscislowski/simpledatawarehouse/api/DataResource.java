package com.maciejscislowski.simpledatawarehouse.api;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "data")
@Data
public class DataResource {
}
