package no.bibsys.entitydata.validation.rdfutils;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

public class RdfsConstants {

    private final static Model model = ModelFactory.createDefaultModel();


    public static final String PROLOGUE = "PREFIX rdfs:<" + RDFS.uri + ">";

    public static final Resource PROPERTY_CLASS = model.createResource(RDFS.uri + "Property");


}
