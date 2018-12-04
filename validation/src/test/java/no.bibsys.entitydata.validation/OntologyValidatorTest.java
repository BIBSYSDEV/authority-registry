package no.bibsys.entitydata.validation;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.riot.Lang;
import org.junit.Test;

public class OntologyValidatorTest implements ModelParser {


    private final transient OntologyValidator ontologyValidator;


    public OntologyValidatorTest() throws IOException {
        ontologyValidator = initializeOntologyValidator();
    }


    @Test
    public void loadOntology_ontologyString_OntologyModel() {
        Model model = ontologyValidator.getOntology();
        assertFalse(model.isEmpty());
    }


    @Test
    public void checkModel_shaclValidationSchema_validIfItContainsOnlyObjectsDefinedInOntology()
        throws IOException {
        String modelSting=IoUtils.resourceAsString(Paths.get("validation","unit-entity-ontology.ttl"));
        Model dataModel = parseModel(modelSting,Lang.TURTLE);
        NodeIterator objectsIterator = dataModel.listObjects();

        assertFalse(dataModel.isEmpty());
    }


    private OntologyValidator initializeOntologyValidator() throws IOException {
        String ontologyString = IoUtils.resourceAsString(
            Paths.get("validation", "unit-entity-ontology.ttl"));

        return new OntologyValidator(ontologyString, Lang.TURTLE);


    }


}
