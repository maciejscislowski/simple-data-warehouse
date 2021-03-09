package com.maciejscislowski.simpledatawarehouse.api.validators;

import java.util.List;

public interface EndpointValidatorDelegate {

    List<String> validateEndpoint(String csvEndpointUrl);

}
