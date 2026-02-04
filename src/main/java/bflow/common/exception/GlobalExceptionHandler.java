package bflow.common.exception;

import bflow.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global controller advice to handle application-wide exceptions.
 */
@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Handles IllegalStateExceptions (e.g., conflicts).
     * @param ex the exception.
     * @param request the current request.
     * @return error response with CONFLICT status.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            final IllegalStateException ex,
            final HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Handles authentication credential failures.
     * @param ex the exception.
     * @param request the current request.
     * @return error response with UNAUTHORIZED status.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            final InvalidCredentialsException ex,
            final HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    /**
     * Handles bean validation errors.
     * @param ex the exception.
     * @param request the current request.
     * @return error response with BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorMsg, request.getRequestURI()));
    }

    /**
     * Final fallback for unhandled exceptions.
     * @param ex the exception.
     * @param request the current request.
     * @return error response with INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(
            final Exception ex,
            final HttpServletRequest request) {
        log.error("UNHANDLED EXCEPTION at {} {}",
                request.getMethod(), request.getRequestURI(), ex);

        ApiResponse<?> response = ApiResponse.error(
                "Internal server error",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
