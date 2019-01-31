package no.bibsys.entitydata.validation.ontology;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class UnitOntology {


    public static String NAMESPACE = "http://unit.no/entitydata#";
    private static final String preferredLabelString = String.join("", NAMESPACE, "preferredLabel");
    public static final Property PREFERRED_LABEL = new PropertyImpl(preferredLabelString);
    private static final String conceptString = String.join("", NAMESPACE, "Concept");
    public static final Resource CONCEPT = new ResourceImpl(conceptString);

}
