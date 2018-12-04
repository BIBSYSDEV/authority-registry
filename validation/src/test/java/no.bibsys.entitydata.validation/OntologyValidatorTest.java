package no.bibsys.entitydata.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
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
        String ontologyModelString = IoUtils
            .resourceAsString(Paths.get("validation", "unit-entity-ontology.ttl"));

        String shaclModelStirng = IoUtils
            .resourceAsString(Paths.get("validation", "validationSchema.ttl"));
        Model shaclModel = parseModel(shaclModelStirng, Lang.TURTLE);
        OntologyValidator validator = new OntologyValidator(ontologyModelString, Lang.TURTLE);
        boolean isValid = validator.isShaclSchemaValid(shaclModel);

        assertTrue(isValid);
    }


    private OntologyValidator initializeOntologyValidator() throws IOException {
        String ontologyString = IoUtils.resourceAsString(
            Paths.get("validation", "unit-entity-ontology.ttl"));

        return new OntologyValidator(ontologyString, Lang.TURTLE);


    }


}
