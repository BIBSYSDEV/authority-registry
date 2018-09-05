package no.bibsys.db.structures;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import java.util.List;

public interface TableDefinitions {

  List<AttributeDefinition> attributeDefinitions();
  List<KeySchemaElement> keySchema();





}
