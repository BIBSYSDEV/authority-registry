package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShaclValidatorTest extends ModelParser {

    private static final String RESOURCES_PATH = "validation";
    private static final String ENTITY_ONTOLOGY_TTL = "unit-entity-ontology.ttl";
    private static final String VALID_SCHEMA = "validShaclValidationSchema.ttl";
    private static final String INVALID_PATH_SCHEMA = "invalidPathObjectShaclValidationSchema.ttl";
    private static final String INVALID_CLASS_SCHEMA = "invalidClassShaclValidationSchema.ttl";
    private static final String INVALID_DATATYPE_SCHEMA = "invalidDatatypeRangeShaclValidationSchema.ttl";
    private static final String INVALID_DOMAIN_SCEMA = "invalidPropertyDomainShaclValidationSchema.ttl";
    private static final String INVALID_PATH_OBJECT_VALIDATION_SCHEMA_TTL =
            "invalidPathObjectShaclValidationSchema.ttl";
    private static final String INVALID_TARGET_CLASS_VALIDATION_SCHEMA_TTL =
            "invalidTargetClassShaclValidationSchema.ttl";

    private final ShaclValidator validator;

    public ShaclValidatorTest() throws IOException {
        validator = initializeOntologyValidator();
    }

    private ShaclValidator initializeOntologyValidator() throws IOException {

        String ontologyString = IoUtils.resourceAsString(Paths.get(RESOURCES_PATH, ENTITY_ONTOLOGY_TTL));

        return new ShaclValidator(ontologyString, Lang.TURTLE);
    }

    @Test
    public void loadOntology_ontologyString_OntologyModel() {

        Model model = validator.getOntology();
        assertFalse(model.isEmpty());
    }

    @Test
    public void shaclModelTargetClassesAreClassesOfOntology_validShaclSchema_valid()
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        boolean result = validator.checkModel(parseModel(VALID_SCHEMA));
        assertTrue(result);
    }

    private Model parseModel(String fileName) throws IOException {
        String modelString = IoUtils.resourceAsString(Paths.get(RESOURCES_PATH, fileName));
        return parseModel(modelString, Lang.TURTLE);

    }

    @Test(expected = ShaclModelTargetClassesAreNotClassesOfOntologyException.class)
    public void shaclModelTargetClassesAreClassesOfOntology_invalidShackValidationSchema_notValid()
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        boolean result = validator.checkModel(parseModel(INVALID_CLASS_SCHEMA));
        assertFalse(result);

    }

    @Test(expected = ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException.class)
    public void shaclModelDatatypeObjectsMapExactlyPropertyRange_invalidDatatypeRangeShaclSchema_notValid()
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {

        assertFalse(validator.checkModel(parseModel(INVALID_DATATYPE_SCHEMA)));
    }

    @Test(expected = ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException.class)
    public void shaclModelTargetClassesAreInDomainOfRespectiveProperties_invalidShaclSchema_valid()
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        assertFalse(validator.checkModel(parseModel(INVALID_DOMAIN_SCEMA)));
    }

    @Test(expected = ShaclModelPathObjectsAreNotOntologyPropertiesException.class)
    public void shaclModelPathObjectsAreOntologyProperties_invalidShaclSchema_valid()
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        assertFalse(validator.checkModel(parseModel(INVALID_PATH_OBJECT_VALIDATION_SCHEMA_TTL)));
    }

    @Test(expected = TargetClassPropertyObjectIsNotAResourceException.class)
    public void checkThatRdfNodesAreResourcesOrThrowException_invalidShaclSchema_Exception()
            throws IOException, TargetClassPropertyObjectIsNotAResourceException, ShaclModelValidationException {
        assertFalse(validator.checkModel(parseModel(INVALID_TARGET_CLASS_VALIDATION_SCHEMA_TTL)));
    }

}
