package no.bibsys.entitydata.validation;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
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

        OntologyValidator validator = initializeOntologyValidator();
        String validdationModelString = IoUtils
            .resourceAsString(Paths.get("validation", "validationSchema.ttl"));
        Model schaclModel = parseModel(validdationModelString, Lang.TURTLE);
        boolean result = validator.checkModel(schaclModel);

        assertTrue(result);

    }


    @Test
    public void listAllowedProperties_void_completeListWithPropertiesInOntology()
        throws IOException {
        OntologyValidator validator = initializeOntologyValidator();
        Set<Resource> properties = validator
            .listAllowedProperties();
        assertThat(properties.size(), is(equalTo(9)));
    }


    @Test
    public void listActualProperties_void_listOfPropertiesInShaclModel()
        throws IOException {
        OntologyValidator validator = initializeOntologyValidator();
        Model shaclModel = validationSchema();
        List<Model> properties = validator.listActualPropertiesModels(shaclModel);

        assertThat(properties.size(), is(equalTo(1)));
    }


    private OntologyValidator initializeOntologyValidator() throws IOException {
        String ontologyString = IoUtils.resourceAsString(
            Paths.get("validation", "unit-entity-ontology.ttl"));

        return new OntologyValidator(ontologyString, Lang.TURTLE);


    }


    private Model validationSchema() throws IOException {
        String validdationModelString = IoUtils
            .resourceAsString(Paths.get("validation", "validationSchema.ttl"));
        Model schaclModel = parseModel(validdationModelString, Lang.TURTLE);
        return schaclModel;
    }


}
