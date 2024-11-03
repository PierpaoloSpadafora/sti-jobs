package unical.demacs.rdm.config.exception;

import java.io.IOException;

public class UserException extends RuntimeException {
    public UserException(String message, IOException e) { super(message); }
}