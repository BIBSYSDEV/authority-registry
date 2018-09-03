package no.bibsys.db;

import no.bibys.db.TableCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DatabaseIntegrationTest extends DynamoTest  {


  @Override
  @BeforeEach
  public void init(){
     ; }


  @Tag("integration")
  @Test
  public void createReadAndWriteIntegrationTest(){
    tableCreator=new TableCreator();

  }




}
