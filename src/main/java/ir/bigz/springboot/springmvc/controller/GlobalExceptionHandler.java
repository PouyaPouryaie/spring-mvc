package ir.bigz.springboot.springmvc.controller;

import ir.bigz.springboot.springmvc.exceptions.ValidationException;
import ir.bigz.springboot.springmvc.model.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<?> validationException(ValidationException e){

        ErrorDetails errorDetails = new ErrorDetails();
        Errors errors = e.getErrors();
        if(errors.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<ObjectError> allErrors = errors.getAllErrors();
            for(ObjectError objectError : allErrors){
                errorMsg.append(objectError.getDefaultMessage() + "\n");
            }
            errorDetails.setErrorMessage(errorMsg.toString());
        }
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> servletRequestBindingException(ServletRequestBindingException e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(e.getMessage());
        errorDetails.setDevErrorMessage(getStackTraceAsString(e));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String getStackTraceAsString(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
