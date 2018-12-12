package no.bibsys.entitydata.validation;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public interface ModelParser {


    default Model parseModel(String jsonString, Lang lang) {
        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, lang);
        return model;

    }


    default List<Resource> getUriResourceObjects(Model model) {
        NodeIterator objects = model.listObjects();
        List<Resource> result = new ArrayList<>();
        RDFNode current;
        while (objects.hasNext()) {
            current = objects.next();
            if (current.isURIResource()) {
                result.add((Resource) current);
            }

        }
        return result;
    }


}
