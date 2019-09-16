package no.bibsys.entitydata.validation.ontology;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class UnitOntology {

    private static final String NAMESPACE_TEMPLATE = "http://unit.no/entitydata#%s";
    private static final String CONCEPT_SCHEME_STRING = "ConseptScheme";
    public static final Resource CONCEPT_SCHEME = ResourceFactory.createResource(String.format(NAMESPACE_TEMPLATE, CONCEPT_SCHEME_STRING));
    private static String NAMESPACE = "http://unit.no/entitydata#";
    private static final String preferredLabelString = String.join("", NAMESPACE, "preferredLabel");
    public static final Property PREFERRED_LABEL = new PropertyImpl(preferredLabelString);
    private static final String conceptString = String.join("", NAMESPACE, "Concept");
    public static final Resource CONCEPT = new ResourceImpl(conceptString);
    private static final String sameAsString = String.join("", NAMESPACE, "sameAs");
    public static final Property SAME_AS = ResourceFactory.createProperty(sameAsString);

}
