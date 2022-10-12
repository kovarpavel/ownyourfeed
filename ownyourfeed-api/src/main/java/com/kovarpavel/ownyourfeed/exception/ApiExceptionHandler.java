package com.kovarpavel.ownyourfeed.exception;

import com.kovarpavel.ownyourfeed.rss.RssApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = UserExistException.class)
    protected ResponseEntity<Object> handleExistingUser(UserExistException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = SourceNotFoundException.class)
    protected ResponseEntity<Object> handleSourceNotFound(SourceNotFoundException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    protected ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = RssApiException.class)
    protected ResponseEntity<Object> handleRssApiException(RssApiException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
