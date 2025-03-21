package com.api.gestion_analyse.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import kong.unirest.UnirestException;

@ControllerAdvice
public class AnalyseApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse> handleAnalyseNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnirestException.class)
    protected ResponseEntity<ApiResponse> handleCouldNotCommunicateWithMS(UnirestException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
