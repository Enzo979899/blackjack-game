package dev.enzo.blackjack.game.infrastructure.in.web;

import dev.enzo.blackjack.player.application.exception.PlayerNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;

@RestControllerAdvice(assignableTypes = RoundController.class)
public class RoundExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidation(WebExchangeBindException exception) {

        String message = exception.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst().orElse("Invalid request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePlayerNotFound(PlayerNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }
}
