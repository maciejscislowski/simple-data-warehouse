package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import com.maciejscislowski.simpledatawarehouse.infrastructure.ActiveProfiles;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

@Slf4j
@ConfigurationProperties(prefix = "app.es")
@Configuration
public class ElasticsearchProperties {

    @Autowired
    private ActiveProfiles activeProfiles;
    @Setter
    @Getter
    private String dataIndexName;
    @Setter
    @Getter
    private String metadataIndexName;
    @Setter
    private String host;
    @Getter
    private URI uri;

    @PostConstruct
    void init() throws URISyntaxException {
        uri = new URI(host);
    }

    public String getUser() {
        return isSecured() ? uri.getUserInfo().split(":")[0] : null;
    }

    public String getPassword() {
        return isSecured() ? uri.getUserInfo().split(":")[1] : null;
    }

    public String getUserAndPassword() {
        return uri.getUserInfo();
    }

    public String getScheme() {
        return isDockerized() ? "http" : uri.getScheme();
    }

    public int getPort() {
        return isDockerized() ? Integer.parseInt(uri.getSchemeSpecificPart()) : uri.getPort();
    }

    public String getHost() {
        return isDockerized() ? uri.getScheme() : uri.getHost();
    }

    public String getFullHost() {
        return isSecured() ? host : isDockerized() ?
                format("%s:%s", uri.getScheme(), uri.getSchemeSpecificPart()) :
                format("%s://%s:%s", uri.getScheme(), uri.getHost(), uri.getPort());
    }

    public boolean isSecured() {
        return activeProfiles.getActiveProfiles().contains("heroku");
    }

    private boolean isDockerized() {
        return activeProfiles.getActiveProfiles().contains("docker");
    }

}
