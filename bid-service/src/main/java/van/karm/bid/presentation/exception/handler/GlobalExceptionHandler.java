package van.karm.bid.presentation.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import van.karm.bid.domain.exception.*;
import van.karm.bid.presentation.dto.response.ApiError;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Переданные значения не прошли валидацию: {}", ex.getMessage());

        String messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return buildError(HttpStatus.BAD_REQUEST, "Validation error", messages, request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Сущность не найдена: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Entity not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.warn("Неизвестная ошибка: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiError> handleUnauthenticatedException(UnauthenticatedException ex, HttpServletRequest request) {
        log.warn("Ошибка авторизации: {}", ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid token", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
        log.warn("Недействительный токен: {}", ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid token", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidArgument(InvalidArgumentException ex, HttpServletRequest request) {
        log.warn("Неверный аргумент: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Invalid argument", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        log.error("Сервис недоступен: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Ошибка доступа для ставки: {}", ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, "Bid denied", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String messages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("Ошибка валидации при обращении к {}: {}", request.getRequestURI(), messages);

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                messages,
                request.getRequestURI()
        );
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
