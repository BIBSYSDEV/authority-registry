package no.bibsys.entitydata.validation.rdfutils;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class RdfConstants {

    public static boolean isNotRDFType(Resource resource) {
        return !resource.equals(RDF.type);
    }
}
