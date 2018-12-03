package no.bibsys.entitydata.validation;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public interface SchemaParser {


    default Model parseJsonLD(String  jsonString){
        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream,Lang.JSONLD);
        return model;


    }



}
