package com.nbc.trello.global.exception;

import com.nbc.trello.global.response.CommonResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({
        IllegalArgumentException.class,
        EntityExistsException.class,
        EntityNotFoundException.class})
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(Exception ex) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
            .msg(ex.getMessage())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .build();
        return ResponseEntity.badRequest().body(response);
    }
}