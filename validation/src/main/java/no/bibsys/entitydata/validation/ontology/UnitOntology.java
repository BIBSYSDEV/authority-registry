package no.bibsys.entitydata.validation.ontology;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.impl.PropertyImpl;

public class UnitOntology {


    public static String  NAMESPACE="http://unit.no/entitydata#";
    private static final String  preferredLabelString= String.join("",NAMESPACE,"preferredLabel");
    public static final Property PREFERRED_LABEL= new PropertyImpl(preferredLabelString);

}
