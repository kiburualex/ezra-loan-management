package world.ezra.loan_management.common.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import world.ezra.loan_management.common.exceptions.ExceptionResponse;
import world.ezra.loan_management.common.exceptions.OperationNotPermittedException;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Alex Kiburu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException exp) {
        log.error("MethodArgumentNotValidException:: ", exp);
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );

        var response = ExceptionResponse.builder()
                .status("01")
                .message(String.join(", ", errors))
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<?> handleException(OperationNotPermittedException exp) {
        log.error("OperationNotPermittedException:: ", exp);
        var response = ExceptionResponse.builder()
                .status("01")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleException(IllegalArgumentException exp) {
        log.error("IllegalArgumentException:: ", exp);
        var response = ExceptionResponse.builder()
                .status("01")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exp) {
        log.error("Exception:: ", exp);
        var response = ExceptionResponse.builder()
                .status("99")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleException(NoSuchElementException exp) {
        log.error("NoSuchElementException:: ", exp);
        var response = ExceptionResponse.builder()
                .status("01")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleException(UsernameNotFoundException exp) {
        log.error("UsernameNotFoundException:: ", exp);
        var response = ExceptionResponse.builder()
                .status("01")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException exp) {
        log.error("BadCredentialsException:: ", exp);
        var response = ExceptionResponse.builder()
                .status("01")
                .message(exp.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
