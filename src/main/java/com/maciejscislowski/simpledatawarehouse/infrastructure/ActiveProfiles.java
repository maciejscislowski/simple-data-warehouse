package com.maciejscislowski.simpledatawarehouse.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class ActiveProfiles {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    public List<String> getActiveProfiles() {
        return Collections.unmodifiableList(asList(activeProfiles.split(",")));
    }
}
