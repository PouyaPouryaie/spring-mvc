package ir.bigz.springboot.springmvc.exceptions;


import org.springframework.validation.Errors;

import java.io.Serial;

public class ValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private Errors errors;

    public ValidationException(Errors errors){
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
