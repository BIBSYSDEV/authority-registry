package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import no.bibsys.responses.SimpleResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DatabaseControllerExcepctionHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = TableAlreadyExistsException.class)
    public ResponseEntity<Object> handleConflict(TableAlreadyExistsException ex,
        WebRequest request) {
        SimpleResponse response = new SimpleResponse("Table already exists");
        return handleExceptionInternal(ex, response,
            new HttpHeaders(), HttpStatus.CONFLICT, request);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = TableNotFoundException.class)
    public ResponseEntity<Object> tableDoesNotExist(TableNotFoundException ex,
        WebRequest request) {
        SimpleResponse response = new SimpleResponse("Table does not exist");
        return handleExceptionInternal(ex, response,
            new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = ConditionalCheckFailedException.class)
    public ResponseEntity<Object> tableDoesNotExist(ConditionalCheckFailedException ex,
        WebRequest request) {
        SimpleResponse response = new SimpleResponse("Could not insert item");
        return handleExceptionInternal(ex, response,
            new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


}



