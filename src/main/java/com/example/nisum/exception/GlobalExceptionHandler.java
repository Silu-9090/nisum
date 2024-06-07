package com.example.nisum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResourceNotFoundException> handleNoHandlerFoundException(NoHandlerFoundException ex) {
		ResourceNotFoundException response = new ResourceNotFoundException(HttpStatus.NOT_FOUND.value(), HttpStatus.BAD_REQUEST.name(), "Requested Resource Not Found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MethodNotAllowedException> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		MethodNotAllowedException response = new MethodNotAllowedException(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.toString(),"Method Not Allowed");
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
	
	@ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<OfferNotFoundException> handleOfferNotFoundException(OfferNotFoundException ex) {
		OfferNotFoundException response = new OfferNotFoundException(ex.getErrorCode(), ex.getType(), ex.getErrorMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}  
