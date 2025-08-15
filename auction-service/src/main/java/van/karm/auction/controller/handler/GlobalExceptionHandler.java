package van.karm.auction.controller.handler;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import van.karm.auction.exception.AccessDeniedException;
import van.karm.auction.exception.InvalidArgumentException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<String> invalidArgumentExceptionHandler(InvalidArgumentException ex) {
        log.warn("Произошла ошибка из-за переданных аргументов: {}",ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid argument exception from client: " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundExceptionHandler(EntityNotFoundException ex) {
        log.warn("Соответствующая сущность не найдена: {}",ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("The corresponding entity was not found: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDeniedExceptionHandler(AccessDeniedException ex) {
        log.warn("Запрос отклонен из-за неправильных кредов: {}",ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("Переданные значения не прошли валидацию: {}", ex.getMessage());

        String messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bad Request: " + messages);
    }

}
