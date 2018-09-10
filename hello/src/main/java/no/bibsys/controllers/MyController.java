package no.bibsys.controllers;

import no.bibsys.handlers.requests.SimpleRequest;
import no.bibsys.handlers.responses.SimpleResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class MyController {


  @RequestMapping(path = "/db", method = RequestMethod.GET)
  public SimpleResponse sayHello(@RequestBody SimpleRequest request) {

    SimpleResponse response = new SimpleResponse("Contoller says hI!!!");

    return response;
  }
//
//
//  @RequestMapping(path = "/hello", method = RequestMethod.GET)
//  public SimpleResponse sayHello2(@RequestBody SimpleRequest request) {
//
//    SimpleResponse response = new SimpleResponse("Contoller says hI!!!");
//
//    return response;
//  }
//
//
//  @RequestMapping(path = "/db", method = RequestMethod.POST)
//  public SimpleResponse readTableName(@RequestBody DatabaseWriteRequest writeRequest) {
//    if (writeRequest.getTableName() == null) {
//      return null;
//    }
//    SimpleResponse response = new SimpleResponse(
//        String.format("The dbname is %", writeRequest.getTableName()));
//
//    return response;
//  }


  @RequestMapping(value = "*", method = RequestMethod.GET)
  @ResponseBody
  public SimpleResponse getFallback() {
    SimpleResponse response = new SimpleResponse("Contoller says hI!!!");
    return response;
  }


}