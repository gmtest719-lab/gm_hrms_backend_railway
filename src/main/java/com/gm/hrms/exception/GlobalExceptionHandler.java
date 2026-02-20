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

    //  RESOURCE NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex) {

        return buildResponse(
                ErrorCode.RESOURCE_NOT_FOUND,
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                null
        );
    }

    //  DUPLICATE RESOURCE
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(
            DuplicateResourceException ex) {

        return buildResponse(
                ErrorCode.DUPLICATE_RESOURCE,
                ex.getMessage(),
                HttpStatus.CONFLICT,
                null
        );
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRequest(InvalidRequestException ex){

        return buildResponse(
                ErrorCode.BAD_REQUEST,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                null
        );
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(Exception ex){

        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }



    //  VALIDATION ERROR
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String,String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                validationErrors.put(err.getField(), err.getDefaultMessage())
        );

        return buildResponse(
                ErrorCode.VALIDATION_FAILED,
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                validationErrors
        );
    }

    //  401 UNAUTHORIZED (Token Missing / Invalid)
    @ExceptionHandler(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(Exception ex){

        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                "Authentication required",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    //  403 ACCESS DENIED
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception ex){

        return buildResponse(
                ErrorCode.FORBIDDEN,
                "Access denied",
                HttpStatus.FORBIDDEN,
                null
        );
    }

    //  404 API NOT FOUND
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handle404(Exception ex){

        return buildResponse(
                ErrorCode.NOT_FOUND,
                "API endpoint not found",
                HttpStatus.NOT_FOUND,
                null
        );
    }

    //  405 METHOD NOT ALLOWED
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(Exception ex){

        return buildResponse(
                ErrorCode.METHOD_NOT_ALLOWED,
                "HTTP method not allowed for this endpoint",
                HttpStatus.METHOD_NOT_ALLOWED,
                null
        );
    }

    //  INVALID JWT TOKEN
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwt(Exception ex){

        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                "Invalid or malformed token",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenNotFound(TokenNotFoundException ex) {
        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpired(TokenExpiredException ex) {
        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                null
        );
    }


    //  GENERIC FALLBACK (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {

        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                null
        );
    }


    //  COMMON BUILDER METHOD (ENTERPRISE CLEAN CODE)
    private ResponseEntity<ApiResponse<Object>> buildResponse(
            ErrorCode code,
            String message,
            HttpStatus status,
            Map<String,String> validationErrors){

        ApiError error = ApiError.builder()
                .errorCode(code.name())
                .errorMessage(message)
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .error(error)
                        .build(),
                status
        );
    }



}
