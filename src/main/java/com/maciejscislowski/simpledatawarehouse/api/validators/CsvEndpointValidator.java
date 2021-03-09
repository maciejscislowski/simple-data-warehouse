package com.maciejscislowski.simpledatawarehouse.api.validators;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static java.lang.String.join;

@Component
public class CsvEndpointValidator implements ConstraintValidator<CsvEndpoint, String>, ApplicationContextAware {

    private static ApplicationContext context;
    private EndpointValidatorDelegate endpointValidatorDelegate;

    @Override
    public void initialize(CsvEndpoint csvEndpoint) {
        endpointValidatorDelegate = context.getBean(EndpointValidatorDelegate.class);
    }

    @Override
    public boolean isValid(String csvEndpointUrl, ConstraintValidatorContext ctx) {
        List<String> errors = endpointValidatorDelegate.validateEndpoint(csvEndpointUrl);
        if (!errors.isEmpty()) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(join(",", errors)).addConstraintViolation();
        }
        return errors.isEmpty();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
