package no.bibsys.entitydata.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

public class OntologyValidator implements ModelParser {


    private final Model ontology;

    public OntologyValidator(String ontologyString, Lang lang)  {
        this.ontology = parseModel(ontologyString, lang);
    }

    public Model getOntology() {
        return ontology;
    }


    public void checkModel(Model model){
            ontology.listSubjects();
    }


}
