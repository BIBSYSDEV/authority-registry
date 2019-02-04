package no.bibsys.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;

public class ModelParser {

    @JsonIgnore
    public Model parseModel(InputStream stream, Lang lang) {

        try {
            Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, stream, lang);
            return model;
        } catch (RiotException e) {
            throw new ValidationSchemaSyntaxErrorException(e);
        }
    }

    public Model parseModel(String dataString, Lang lang) {
        try {
            InputStream stream = new ByteArrayInputStream(dataString.getBytes(StandardCharsets.UTF_8));
            return parseModel(stream, lang);
        } catch (RiotException e) {
            throw new ValidationSchemaSyntaxErrorException(e);
        }
    }

    @JsonIgnore
    public Set<Resource> getUriResourceObjects(Model model) {
        return model.listObjects().toSet().stream().filter(RDFNode::isURIResource).map(rdfNode -> (Resource) rdfNode)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public String writeData(Model model, Lang lang) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, lang);
        return writer.toString();
    }

}
