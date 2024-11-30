package unical.demacs.rdm.config.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import unical.demacs.rdm.config.exception.*;

import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    private final ObjectMapper objectMapper;

    public ExceptionsHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<String> handleTooManyRequests() {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", "Too Many Requests");
        return new ResponseEntity<>(response.toString(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<String> handleNoUserFoundException(NoUserFoundException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("error", "No user found");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<String> handleScheduleNotFoundException(ScheduleNotFoundException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<String> handleScheduleException(ScheduleException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<String> handleJobNotFoundException(JobNotFoundException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobException.class)
    public ResponseEntity<String> handleJobException(JobException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MachineNotFoundException.class)
    public ResponseEntity<String> handleMachineNotFoundException(MachineNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateMachineNameException.class)
    public ResponseEntity<ObjectNode> handleDuplicateMachineNameException(DuplicateMachineNameException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MachineException.class)
    public ResponseEntity<String> handleMachineException(MachineException ex) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
