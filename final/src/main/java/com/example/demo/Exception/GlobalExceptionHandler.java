package com.example.demo.Exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,Object>> handleBadCredentials(BadCredentialsException ex){
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDenied(AccessDeniedException ex){
        return buildResponse(HttpStatus.FORBIDDEN, "Accès refusé");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleNotReadable(HttpMessageNotReadableException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Format de requête invalide");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String,Object>> handleMissingParam(MissingServletRequestParameterException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Paramètre manquant : " + ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, "Type de paramètre invalide pour : " + ex.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntime(RuntimeException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAllExceptions(Exception ex){
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
    }

    private ResponseEntity<Map<String,Object>> buildResponse(HttpStatus status, String message) {
        Map<String,Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
