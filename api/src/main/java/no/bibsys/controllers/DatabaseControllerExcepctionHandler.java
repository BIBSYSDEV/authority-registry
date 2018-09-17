package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
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
  public ResponseEntity<Object> handleConflict(TableAlreadyExistsException ex, WebRequest request) {
    SimpleResponse response = new SimpleResponse("Table already exists");
    return handleExceptionInternal(ex, response,
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }
}



