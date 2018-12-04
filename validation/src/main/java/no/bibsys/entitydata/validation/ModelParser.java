package no.bibsys.entitydata.validation;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public interface ModelParser {


    default Model parseModel(String modelString, Lang lang) {
        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(modelString.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, lang);
        return model;

    }


    default Set<Resource> getObjects(Model model) {

        return model.listObjects()
            .filterKeep(RDFNode::isURIResource)
            .mapWith(rdfNode -> (Resource) rdfNode)
            .toSet();

    }


}
