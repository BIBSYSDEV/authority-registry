package no.bibsys.testtemplates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public abstract class SampleData {

    protected ObjectMapper mapper = new ObjectMapper();

    protected Entry sampleEntry(String id) {

        ObjectNode root = mapper.getNodeFactory().objectNode();
        root.put("id", id);
        root.put("label", "A random label");
        root.put("number", 5);
        ArrayNode array = root.putArray("myArray");
        array.add(1);
        array.add(2);
        array.add(3);

        return new Entry(id, root);
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
