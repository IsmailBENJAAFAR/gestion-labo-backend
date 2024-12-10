package com.api.analyse.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

public class AnalyseApiExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse> handleAnalyseNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResponse> handleBadAnalyseBodyRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
