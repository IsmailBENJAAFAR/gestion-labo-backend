package com.api.gestion_laboratoire.errors;

import javax.naming.CommunicationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class LaboApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse> handleLaboNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResponse> handleBadLaboBodyRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommunicationException.class)
    protected ResponseEntity<ApiResponse> handleBadLaboBodyRequest(CommunicationException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()),
                HttpStatus.REQUEST_TIMEOUT);
    }
}
