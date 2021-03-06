package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Simple backend application that exposes data - extracted from a csv file - via a API",
                version = "1.0",
                contact = @Contact(
                        name = "API Support Team",
                        email = "api-support-team@company.com"
                )
        ),
        tags = {
                @Tag(name = "extract", description = "Extract data from a csv file"),
                @Tag(name = "query", description = "General search allowing querying the data in a generic and efficient way using Elasticsearch queries"),
                @Tag(name = "predefined-queries", description = "Queries which use predefined Elasticsearch queries under the hood"),
        },
        servers = {
                @Server(url = "https://simple-data-warehouse-dev.herokuapp.com", description = "DEV")
        }
)
@Configuration
class OpenApiConfig {
}
