package com.example.application.controllers;

import com.example.application.exceptions.UserExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(UsernameNotFoundException e, WebRequest request) {
        return new ResponseEntity<>(getBody(request, e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler({
//            UserExistException.class,
//            UsernameNotFoundException.class,
//            BadCredentialsException.class
//    })
    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<Object> handleExceptionsBadRequest(MethodArgumentNotValidException e, WebRequest request) {
        return new ResponseEntity<>(getBody(request, e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> getBody(WebRequest request, Exception exception, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", exception.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return body;
    }

}