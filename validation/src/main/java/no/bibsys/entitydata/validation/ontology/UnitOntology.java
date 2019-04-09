package no.bibsys.entitydata.validation.ontology;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class UnitOntology {

    private static String NAMESPACE = "http://unit.no/entitydata#";
    private static final String preferredLabelString = String.join("", NAMESPACE, "preferredLabel");
    private static final String conceptString = String.join("", NAMESPACE, "Concept");

    public static final Property PREFERRED_LABEL = new PropertyImpl(preferredLabelString);
    public static final Resource CONCEPT = new ResourceImpl(conceptString);

}
