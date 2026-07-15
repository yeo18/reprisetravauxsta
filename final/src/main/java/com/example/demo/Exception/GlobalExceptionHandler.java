package com.example.demo.Exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,Object>> handleBadCredentials(BadCredentialsException ex){

        Map<String,Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 401);
        response.put("error", "Unauthorized");
        response.put("message", "Email ou mot de passe incorrect");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDenied(AccessDeniedException ex){

        Map<String,Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 403);
        response.put("error", "Forbidden");
        response.put("message", "Accès refusé");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAllExceptions(Exception ex){

        Map<String,Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}