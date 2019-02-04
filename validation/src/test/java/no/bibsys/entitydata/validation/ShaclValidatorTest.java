package no.bibsys.entitydata.validation;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPropertiesAreNotIcludedInOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;

public class ShaclValidatorTest extends ModelParser {

    private static final String RESOURCES_PATH = "validation";
    private static final String ENTITY_ONTOLOGY_TTL = "unit-entity-ontology.ttl";
    private static final String VALID_SCHEMA = "validShaclValidationSchema.ttl";
    private static final String INVALID_PATH_SCHEMA = "invalidPathObjectShaclValidationSchema.ttl";
    private static final String INVALID_CLASS_SCHEMA = "invalidClassShaclValidationSchema.ttl";
    private static final String INVALID_DATATYPE_SCHEMA = "invalidDatatypeRangeShaclValidationSchema.ttl";
    private static final String INVALID_DOMAIN_SCEMA = "invalidPropertyDomainShaclValidationSchema.ttl";

    private final ShaclValidator validator;

    public ShaclValidatorTest() throws IOException {
        validator = initializeOntologyValidator();
    }

    private ShaclValidator initializeOntologyValidator() throws IOException {

        String ontologyString = IoUtils.resourceAsString(Paths.get(RESOURCES_PATH, ENTITY_ONTOLOGY_TTL));

        return new ShaclValidator(ontologyString, Lang.TURTLE);
    }

    @Test
    public void loadOntology_ontologyString_OntologyModel() throws IOException {

        Model model = validator.getOntology();
        assertFalse(model.isEmpty());
    }

    @Test
    public void shaclModelTargetClassesAreClassesOfOntology_validShaclSchema_valid()
        throws IOException, ShaclModelValidationException {
        boolean result = validator.checkModel(parseModel(VALID_SCHEMA));
        assertTrue(result);

    }

    private Model parseModel(String fileName) throws IOException {
        String modelString = IoUtils.resourceAsString(Paths.get(RESOURCES_PATH, fileName));
        return parseModel(modelString, Lang.TURTLE);

    }

    @Test(expected = ShaclModelTargetClassesAreNotClassesOfOntologyException.class)
    public void shaclModelTargetClassesAreClassesOfOntology_invalidShackValidationSchema_notValid()
        throws IOException, ShaclModelValidationException {
        boolean result = validator.checkModel(parseModel(INVALID_CLASS_SCHEMA));
        assertFalse(result);

    }

    @Test(expected = ShaclModelPropertiesAreNotIcludedInOntologyException.class)
    public void shaclModelPathObjectsAreOntologyProperties_invalidShaclShchema_notValid()
        throws IOException, ShaclModelValidationException {
        assertFalse(validator.checkModel(parseModel(INVALID_PATH_SCHEMA)));
    }

    @Test(expected = ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException.class)
    public void shaclModelDatatypeObjectsMapExactlyPropertyRange_invalidDatatypeRangeShaclSchema_notValid()
        throws IOException, ShaclModelValidationException {

        assertFalse(validator.checkModel(parseModel(INVALID_DATATYPE_SCHEMA)));
    }

    @Test(expected = ShaclModelValidationException.class)
    public void shaclModelTargetClassesAreInDomainOfRespectiveProperties_invalidShaclSchema_valid()
        throws IOException, ShaclModelValidationException {
        assertFalse(validator.checkModel(parseModel(INVALID_DOMAIN_SCEMA)));
    }

}