package com.gm.hrms.exception;

import com.gm.hrms.enums.ErrorCode;
import com.gm.hrms.payload.ApiError;
import com.gm.hrms.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex) {

        ApiError error = ApiError.builder()
                .errorCode(ErrorCode.RESOURCE_NOT_FOUND.name())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Resource not found")
                        .error(error)
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    //  Duplicate Resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(
            DuplicateResourceException ex) {

        ApiError error = ApiError.builder()
                .errorCode(ErrorCode.DUPLICATE_RESOURCE.name())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Duplicate resource")
                        .error(error)
                        .build(),
                HttpStatus.CONFLICT
        );
    }

    //  Validation Errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiError apiError = ApiError.builder()
                .errorCode(ErrorCode.VALIDATION_FAILED.name())
                .errorMessage("Validation failed")
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Validation error")
                        .error(apiError)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    //  Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {

        ApiError error = ApiError.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message("Something went wrong")
                        .error(error)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
