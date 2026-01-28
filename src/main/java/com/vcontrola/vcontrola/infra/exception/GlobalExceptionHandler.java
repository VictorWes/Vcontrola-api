package com.vcontrola.vcontrola.infra.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<String> tratarErroRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
}
