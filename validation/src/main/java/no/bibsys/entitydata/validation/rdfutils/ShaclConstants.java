package no.bibsys.entitydata.validation.rdfutils;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * the Shacl class http://www.w3.org/ns/shacl#property.
 *
 * <p>
 * Example:
 * <pre>
 *       {@code
 *       :myClass a sh:NodeShape .
 *       :myClass sh:targetClass :ConceptScheme.
 *       :myClass sh:property [
 *           sh:path :preferredLabel ;
 *           sh:datatype xsd:string;
 *        ]
 *       }
 *       </pre>
 * </p>
 */


public class ShaclConstants {

    public static final String NAMESPACE = "http://www.w3.org/ns/shacl#";

    public static final String PROLOGUE = "PREFIX sh:<" + NAMESPACE + ">";


    private static final Model model = ModelFactory.createDefaultModel();

    public static final Property TARGETCLASS_PROPERTY = model
        .createProperty(NAMESPACE, "targetClass");


    public static final Property PROPERTY = model.createProperty(NAMESPACE + "property");


    public static final Property PATH = model.createProperty(NAMESPACE, "path");

    public static final Property DATATYPE = model.createProperty(NAMESPACE, "datatype");


    public static final Resource NODESHAPE = model.createResource(NAMESPACE + "NodeShape");

}
