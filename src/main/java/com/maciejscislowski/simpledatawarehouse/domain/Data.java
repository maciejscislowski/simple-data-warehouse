package com.maciejscislowski.simpledatawarehouse.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@lombok.Data
@Builder
@Mapping(mappingPath = "es/data-mappings.json")
@Setting(settingPath = "es/data-settings.json")
@Document(indexName = "#{@dataIndexName}")
public class Data {

    @Id
    private String id;
    private String datasource;
    private String campaign;
    //    @Field(type = FieldType.Date, format = DateFormat.custom)
//    private LocalDate daily;
    private String daily;
    private Long clicks;
    private Long impressions;

}
