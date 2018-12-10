package no.bibsys.entitydata.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import sun.nio.cs.ext.MacArabic;

public class ShaclConstants {

    public static final String NAMESPACE = "http://www.w3.org/ns/shacl#";
    private static final Model model = ModelFactory.createDefaultModel();
    /**
     * the Shacl property http://www.w3.org/ns/shacl#targetClass Example: :myClass a sh:NodeShape .
     * :myClass sh:targetClass :ConceptScheme.
     */
    public static final Property TARGETCLASS_PROPERTY = model
        .createProperty(NAMESPACE, "targetClass");


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

    public static final Property PROPERTY = model.createProperty(NAMESPACE + "property");


    public static final Property PATH = model.createProperty(NAMESPACE, "path");

    public static final Property DATATYPE = model.createProperty(NAMESPACE, "datatype");


}
