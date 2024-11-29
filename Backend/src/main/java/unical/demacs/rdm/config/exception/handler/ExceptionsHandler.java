package unical.demacs.rdm.config.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import unical.demacs.rdm.config.exception.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    private final ObjectMapper objectMapper;

    public ExceptionsHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
    public ResponseEntity<String> handleNoUserFoundException(NoUserFoundException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("error", "No user found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.toString());
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    private ResponseEntity<?> handleScheduleNotFoundExceptionException(ScheduleNotFoundException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ScheduleException.class)
    private ResponseEntity<?> handleScheduleExceptionException(ScheduleException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JobNotFoundException.class)
    private ResponseEntity<?> handleJobNotFoundExceptionException(JobNotFoundException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobException.class)
    private ResponseEntity<?> handleJobExceptionException(JobException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MachineException.class)
    private ResponseEntity<?> handleMachineExceptionException(MachineException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MachineNotFoundException.class)
    private ResponseEntity<?> handleMachineNotFoundExceptionException(MachineNotFoundException ex) {
        return new ResponseEntity<>(new JSONObject(
                Map.of("message", ex.getMessage())).toString(), HttpStatus.NOT_FOUND);
    }
}