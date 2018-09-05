package no.bibsys.db.structures;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import java.util.ArrayList;
import java.util.List;


public interface Entry  extends TableDefinitions {

  @DynamoDBHashKey(attributeName = "id")
  String getId();

  void setId(String id);


  default List<AttributeDefinition> attributeDefinitions() {
    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
    attributeDefinitions.add(new AttributeDefinition("id", "S"));
//    attributeDefinitions.add(new AttributeDefinition("preferredLabels","R"));
    return attributeDefinitions;
  }


  default List<KeySchemaElement> keySchema() {
    ArrayList<KeySchemaElement> keys = new ArrayList<KeySchemaElement>();
    keys.add(new KeySchemaElement("id", KeyType.HASH));
//    keys.add(new KeySchemaElement("preferredLabels", KeyType.RANGE));
    return keys;
  }


}
