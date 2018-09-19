package no.bibsys.db.structures;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import java.util.ArrayList;
import java.util.List;


public interface Entry extends TableDefinitions {

    @DynamoDBHashKey(attributeName = "id")
    String getId();

    void setId(String id);


    /**
     * The number of attributes in key schema must match the number of attributesdefined in
     * attribute definitions.
     *
     * @return A list of AttributeDefintions
     */
    @Override
    default List<AttributeDefinition> attributeDefinitions() {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition("id", "S"));
        //    attributeDefinitions.add(new AttributeDefinition("name","S"));
        return attributeDefinitions;
    }


    /**
     * The number of attributes in key schema must match the number of attributesdefined in
     * attribute definitions.
     *
     * @return A list of KeySchemaElement
     */
    @Override
    default List<KeySchemaElement> keySchema() {
        List<KeySchemaElement> keys = new ArrayList<KeySchemaElement>();
        keys.add(new KeySchemaElement("id", KeyType.HASH));
        //    keys.add(new KeySchemaElement("name", KeyType.RANGE));
        return keys;
    }


}
