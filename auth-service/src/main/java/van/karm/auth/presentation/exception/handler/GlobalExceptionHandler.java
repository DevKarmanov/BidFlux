package van.karm.auth.presentation.exception.handler;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import van.karm.auth.presentation.dto.error.ApiError;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Пользователь не найден: {} (URI: {})", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "User not found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        log.warn("Неверные учетные данные при обращении к {}", request.getRequestURI());
        return buildError(HttpStatus.UNAUTHORIZED, "Bad credentials", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiError> handleEntityExists(
            EntityExistsException ex,
            HttpServletRequest request
    ) {
        log.warn("Сущность уже существует: {} (URI: {})", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.CONFLICT, "Entity exists", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Неожиданная ошибка при обращении к {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ){
        log.warn("Невалидный refresh токен: {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid token", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(
            IllegalArgumentException ex,
            HttpServletRequest request
    ){
        log.warn("Невалидные аргументы: {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.BAD_REQUEST, "Invalid arguments", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledUser(
            DisabledException ex,
            HttpServletRequest request
    ) {
        log.warn("Заблокированный пользователь: {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Account disabled",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedToken(
            MalformedJwtException ex,
            HttpServletRequest request
    ){
        log.warn("Ошибка при разборе токена: {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.UNAUTHORIZED, "Invalid token", "You provided an incorrect token, and there was a problem parsing it", request.getRequestURI());
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
