package van.karm.complaint.presentation.handler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import van.karm.complaint.presentation.dto.response.ApiError;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().getFirst().getFieldName();
            String expectedType = ife.getTargetType().getSimpleName();
            String message = String.format(
                    "Invalid value for field '%s'. Expected type: %s. Provided: %s",
                    fieldName,
                    expectedType,
                    ife.getValue()
            );
            log.warn("Произошла ошибка десериализации даты: {}", ex.getMessage());
            return buildError(
                    HttpStatus.BAD_REQUEST,
                    "Invalid format",
                    message,
                    request.getRequestURI()
            );
        }
        log.warn("Произошла ошибка десериализации JSON: {}", ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON",
                ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Сущность не найдена: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Entity not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiError> handleEntityExists(EntityExistsException ex, HttpServletRequest request) {
        log.warn("Сущность уже существует: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, "Entity already exists", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Некорректные аргументы: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Illegal arguments", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Неожиданная ошибка при обращении к {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex.getMessage(), request.getRequestURI());
    }


    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        ApiError apiError = new ApiError(
                status.value(),
                error,
                message,
                path,
                Instant.now()
        );
        return ResponseEntity.status(status).body(apiError);
    }
}
