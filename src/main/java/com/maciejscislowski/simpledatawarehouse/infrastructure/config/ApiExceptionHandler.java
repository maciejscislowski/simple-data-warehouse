package com.maciejscislowski.simpledatawarehouse.infrastructure.config;

import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> constraintViolation(ConstraintViolationException ex) {
        return error(ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({FeignException.class, NumberFormatException.class})
    public Map<String, String> apiInput(Exception ex) {
        return error("Error occurred " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler
    public Map<String, String> unsupportedOperation(UnsupportedOperationException ex) {
        return error("Operation is unsupported");
    }

    private Map<String, String> error(String... message) {
        return ImmutableMap.of("error", String.join(",", message));
    }

}
