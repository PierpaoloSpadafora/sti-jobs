package org.unical.demacs.rdm.config.exception.handler;


import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.unical.demacs.rdm.config.exception.NoUserFoundException;
import org.unical.demacs.rdm.config.exception.TooManyRequestsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.unical.demacs.rdm.config.exception.UserException;

import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(TooManyRequestsException.class)
    private ResponseEntity<?> handleTooManyRequests() {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", "Too Many Request")).toString(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(UserException.class)
    private ResponseEntity<?> handleUserExceptionException(UserException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoUserFoundException.class)
    private ResponseEntity<?> handleNoUserFoundExceptionException(NoUserFoundException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.NOT_FOUND);
    }
}
