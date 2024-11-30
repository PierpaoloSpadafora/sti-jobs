package unical.demacs.rdm.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateMachineNameException extends RuntimeException {
    public DuplicateMachineNameException(String message) {
        super(message);
    }
}