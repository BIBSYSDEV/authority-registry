package no.bibsys.testtemplates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class SampleData {

    public SampleData() {
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public Entry sampleEntry(String id) {

        ObjectNode root = getMapper().getNodeFactory().objectNode();
        root.put("id", id);
        root.put("label", "A random label");
        root.put("number", 5);
        ArrayNode array = root.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);

        return new Entry(id, root);
    }


    public ObjectMapper getMapper() {
        return mapper;
    }


    public static class Entry {

        public final String id;

        public final ObjectNode root;

        public Entry(String id, ObjectNode root) {
            this.id = id;
            this.root = root;
        }


        public String jsonString() {
            return root.toString();
        }
    }

}
