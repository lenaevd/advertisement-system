package com.lenaevd.advertisements.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public static final String HANDLED_EXCEPTION_MESSAGE = "Handled [{}], message = {}";

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(Exception ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Unable to register: " + ex.getMessage() +
                "\nPlease enter another " + ex.getNonUniqueField(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<String> handleWrongCredentialsException(Exception ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Unable to login: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<String> handleObjectNotFoundException(Exception ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Incorrect request: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoRightsException.class)
    public ResponseEntity<String> handleNoRightsException(Exception ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("No rights to perform the action: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ActionIsImpossibleException.class)
    public ResponseEntity<String> handleActionIsImpossibleException(Exception ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Action can't be executed: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        errors.add("Arguments not valid:");
        errors.addAll(ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + " " + errorMessage;
                })
                .toList());
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Required parameter is missing: " + ex.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Incorrect data: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.info(HANDLED_EXCEPTION_MESSAGE, ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>("Required parameter is missing: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAnyException(Exception ex) {
        LOGGER.error("Handled exception {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
