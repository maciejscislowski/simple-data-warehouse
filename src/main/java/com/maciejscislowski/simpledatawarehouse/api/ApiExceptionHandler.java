package com.maciejscislowski.simpledatawarehouse.api;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> handle(ConstraintViolationException ex) {
        return error(ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler
    public void handle(UnsupportedOperationException ex) {
    }

    private Map<String, String> error(String... message) {
        return ImmutableMap.of("error", String.join(",", message));
    }

}
