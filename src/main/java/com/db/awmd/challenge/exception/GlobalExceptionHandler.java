package com.db.awmd.challenge.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	    HttpHeaders headers, HttpStatus status, WebRequest request) {

	Map<String, Object> body = new LinkedHashMap<>();
	body.put("status", status.value());

	List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
		.collect(Collectors.toList());

	body.put("errors", errors);

	return new ResponseEntity<>(body, headers, status);

    }

    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<Object> handleUserNotRegisteredException(UserNotRegisteredException ex, WebRequest request) {

	HashMap<String, Object> body = new HashMap<>();
	body.put("message", ex.getMessage());
	body.put("error code", 4111);
	return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex, WebRequest request) {

	HashMap<String, Object> body = new HashMap<>();
	body.put("message", ex.getMessage());
	body.put("error code", 4111);
	return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalenceException.class)
    public ResponseEntity<Object> handleInsufficientBalenceException(InsufficientBalenceException ex,
	    WebRequest request) {

	HashMap<String, Object> body = new HashMap<>();
	body.put("message", ex.getMessage());
	body.put("error code", 4333);
	return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
