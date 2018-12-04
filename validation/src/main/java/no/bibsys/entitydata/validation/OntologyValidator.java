package no.bibsys.entitydata.validation;

import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

public class OntologyValidator implements ModelParser {


    private final Model ontology;

    public OntologyValidator(String ontologyString, Lang lang)  {
        this.ontology = parseModel(ontologyString, lang);
    }

    public Model getOntology() {
        return ontology;
    }


    public boolean isShaclSchemaValid(Model model) {
        Set<Resource> allowedObjects = ontology.listSubjects().toSet();
        Set<Resource> modelObjects = getObjects(model);
        for (Resource modelObject : modelObjects) {
            if (!allowedObjects.contains(modelObject)) {
                return false;
            }
        }
        return true;

    }


}
