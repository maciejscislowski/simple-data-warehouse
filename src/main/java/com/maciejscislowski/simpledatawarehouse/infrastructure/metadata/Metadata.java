package com.maciejscislowski.simpledatawarehouse.infrastructure.metadata;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@lombok.Data
@Builder
@Mapping(mappingPath = "es/metadata-mappings.json")
@Setting(settingPath = "es/metadata-settings.json")
@Document(indexName = "#{@elasticsearchProperties.getMetadataIndexName()}")
public class Metadata {

    @Id
    private String id;
    private boolean etlProcessRunning;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute)
    private LocalDateTime etlProcessLastStarted;

    public Metadata updateEtlProcessLastStarted(LocalDateTime dateTime) {
        this.etlProcessLastStarted = dateTime;
        return this;
    }

    public Metadata updateEtlProcessRunning(boolean running) {
        this.etlProcessRunning = running;
        return this;
    }

}
