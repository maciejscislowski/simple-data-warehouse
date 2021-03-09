package com.maciejscislowski.simpledatawarehouse.api.validators;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, EndpointValidatorGroup.class})
public interface EndpointValidatorSequence {
}
