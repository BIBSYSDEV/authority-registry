package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import com.amazonaws.services.dynamodbv2.model.TableNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import no.bibsys.responses.SimpleResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DatabaseControllerExcepctionHandler extends ResponseEntityExceptionHandler {


    ObjectMapper mapper = new ObjectMapper();

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
    public ResponseEntity<Object> itemAlreadyExists(ConditionalCheckFailedException ex,
        WebRequest request) throws IOException {
        String id = null;
        String body = requestBody(request);
        id = mapper.readValue(body, HashMap.class).getOrDefault("id", "null").toString();

        SimpleResponse response = new SimpleResponse(String.format("Item %s already exists", id));
        return handleExceptionInternal(ex, response,
            new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    private String requestBody(WebRequest request) throws IOException {
        String body = null;
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            HttpServletRequest httpRequest = servletWebRequest.getRequest();
            BufferedReader reader = httpRequest.getReader();
            List<String> lines = reader.lines().collect(Collectors.toList());
            body = String.join("", lines);

        }
        return body;
    }


}


