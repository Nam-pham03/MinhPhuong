package com.lgcns.aidd.handler;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lgcns.aidd.exception.ConstraintViolationException;
import com.lgcns.aidd.exception.ErrorMessage;
import com.lgcns.aidd.exception.NotFoundException;
import com.lgcns.aidd.exception.UserError;
import com.lgcns.aidd.utils.MessageUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.List;

@ControllerAdvice
public class DefaultExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolationExceptionHandler(ConstraintViolationException e) {
        return sendError(HttpStatus.BAD_REQUEST, e.getErrorMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundExceptionHandler(NotFoundException e) {
        var errorMessage = ErrorMessage.builder()
                .addMessage(e.getMessage())
                .build();
        return sendError(HttpStatus.NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        var builder = ErrorMessage.builder();
        var fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .toList();

        for(var fieldError : fieldErrors) {
            builder.addErrors(UserError.builder()
                    .message(fieldError.getDefaultMessage())
                    .fields(List.of(fieldError.getField()))
                    .code(fieldError.getCode())
                    .build());

        }
        return sendError(HttpStatus.BAD_REQUEST, builder.build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> missingServletRequestParameterHandler(MissingServletRequestParameterException e) {
       return sendError(HttpStatus.BAD_REQUEST, ErrorMessage.builder()
               .addErrors(
                       UserError.builder()
                               .code("required")
                               .fields(List.of(e.getParameterName()))
                               .message("Missing parameter " + e.getParameterName())
                               .build()
               )
               .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> messageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        var cause = e.getCause();
        ErrorMessage errorMessage = null;
        if (cause != null) {
            if (cause instanceof JsonParseException) {
                errorMessage = ErrorMessage.builder()
                        .addMessage("Invalid Json formats")
                        .build();
            } else if (cause instanceof JsonMappingException exception) {
                errorMessage = MessageUtils.toError(exception);
            }
        }

        if(errorMessage == null) {
            errorMessage = ErrorMessage.builder()
                    .addMessage("Can't read request")
                    .build();
        }

        return sendError(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> requestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return sendError(HttpStatus.METHOD_NOT_ALLOWED, ErrorMessage.builder()
                .addMessage("Method not allowed")
                .build());
    }

    private ResponseEntity<ErrorMessage> sendError(HttpStatus status, ErrorMessage errorMessage) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(errorMessage);
    }

}
