package no.bibsys.entitydata.validation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class ModelParser {

    protected Model loadData(String rdfString, Lang lang) {
        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(rdfString.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, lang);
        return model;
    }

    protected Set<Resource> getUriResourceObjects(Model model) {
        return model.listObjects()
            .toSet().stream()
            .filter(RDFNode::isURIResource)
            .map(rdfNode -> (Resource) rdfNode)
            .collect(Collectors.toSet());
    }
}
