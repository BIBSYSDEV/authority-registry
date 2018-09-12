package no.bibsys.controllers;

import com.amazonaws.services.dynamodbv2.model.TableAlreadyExistsException;
import no.bibsys.db.DatabaseManager;
import no.bibsys.handlers.CreateRegistryRequest;
import no.bibsys.responses.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
public class MyController {


  final
  DatabaseManager databaseManager;

  @Autowired
  public MyController(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @PostMapping(path = "/registry/create", produces = "application/json;charset=UTF-8")
  public SimpleResponse createRegistry(@RequestBody CreateRegistryRequest request)
      throws InterruptedException, TableAlreadyExistsException {
    String tableName = request.getRegistryName();
    databaseManager.createRegistry(tableName);
    return new SimpleResponse(
        String.format("The registry name is %s", tableName));
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
  public SimpleResponse getFallback() {
    return new SimpleResponse("Controller says hI!!!");
  }


}