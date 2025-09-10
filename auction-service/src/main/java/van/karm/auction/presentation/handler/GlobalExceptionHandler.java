package van.karm.auction.presentation.handler;

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
import van.karm.auction.presentation.dto.exception.ApiError;
import van.karm.auction.domain.exception.AccessDeniedException;
import van.karm.auction.domain.exception.InvalidArgumentException;
import van.karm.auction.domain.exception.UnauthenticatedException;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiError> invalidArgumentExceptionHandler(
            InvalidArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Произошла ошибка из-за переданных аргументов: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Invalid arguments", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundExceptionHandler(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Соответствующая сущность не найдена: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Entity not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accessDeniedExceptionHandler(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Запрос отклонен из-за недостатка прав: {}", ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiError> unauthenticatedExceptionHandler(
            UnauthenticatedException ex,
            HttpServletRequest request
    ) {
        log.warn("Запрос отклонен из-за проблем с аутентификацией: {}", ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), request.getRequestURI());
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Неожиданная ошибка при обращении к {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex.getMessage(), request.getRequestURI());
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
