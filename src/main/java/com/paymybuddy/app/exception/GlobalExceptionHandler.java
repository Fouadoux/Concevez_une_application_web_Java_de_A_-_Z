package com.paymybuddy.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntitySaveException.class)
    public ResponseEntity<Map<String, String>> handleEntitySaveException(EntitySaveException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEmailException(InvalidEmailException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleRoleAlreadyExistsException(RoleAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBalanceException(InvalidBalanceException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("details", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put("error", error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}
