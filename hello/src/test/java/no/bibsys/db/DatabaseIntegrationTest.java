package no.bibsys.db;

import no.bibys.db.TableCreator;
import no.bibys.db.TableDriverFactory;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

public class DatabaseIntegrationTest extends DynamoTest  {


  @Override
  @BeforeEach
  public void init(){
     ; }


  @Tag("integration")
  @Ignore
  public void createReadAndWriteIntegrationTest(){
    tableCreator= TableCreator.create(new TableDriverFactory());

  }




}
