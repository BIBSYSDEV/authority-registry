package no.bibsys.entitydata.validation;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntology;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

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

    @Test(expected = ShaclModelTargetClassesAreNotClassesOfOntology.class)
    public void shaclModelTargetClassesAreClassesOfOntology_invalidShackValidationSchema_notValid()
        throws IOException, ShaclModelValidationException {
        boolean result = validator.checkModel(parseModel(INVALID_CLASS_SCHEMA));
        assertFalse(result);

    }

    @Test
    public void shaclModelPathObjectsAreOntologyProperties_validShaclShchema_valid()
        throws IOException, ShaclModelValidationException {
        assertTrue(validator.checkModel(parseModel(VALID_SCHEMA)));

    }

//    @Test
//    public void shaclModelPathObjectsAreOntologyProperties_invalidShaclShchema_notValid()
//        throws IOException {
//
//        ShaclValidator validator = initializeOntologyValidator(INVALID_PATH_SCHEMA);
//        assertFalse(validator.shaclModelPathObjectsAreOntologyProperties());
//        assertFalse(validator.checkModel());
//    }
//
//
//    @Test
//    public void shaclModelDatatypeObjectsMapExactlyPropertyRange_validShackSchema_valid()
//        throws IOException {
//
//        ShaclValidator validator = initializeOntologyValidator(VALID_SCHEMA);
//        assertTrue(validator.shaclModelDatatypeObjectsMapExactlyPropertyRange());
//        assertTrue(validator.checkModel());
//    }
//
//    @Test
//    public void shaclModelDatatypeObjectsMapExactlyPropertyRange_invalidDatatypeRangeShaclSchema_notValid()
//        throws IOException {
//
//        ShaclValidator validator = initializeOntologyValidator(INVALID_DATATYPE_SCHEMA);
//        assertFalse(validator.shaclModelDatatypeObjectsMapExactlyPropertyRange());
//        assertFalse(validator.checkModel());
//    }
//
//    @Test
//    public void shaclModelTargetClassesAreInDomainOfRespectiveProperties_validShaclSchema_valid()
//        throws IOException {
//
//        ShaclValidator validator = initializeOntologyValidator(VALID_SCHEMA);
//        assertTrue(validator.shaclModelTargetClassesAreInDomainOfRespectiveProperties());
//        assertTrue(validator.checkModel());
//    }
//
//    @Test
//    public void shaclModelTargetClassesAreInDomainOfRespectiveProperties_invalidShaclSchema_valid()
//        throws IOException {
//
//        ShaclValidator validator = initializeOntologyValidator(INVALID_DOMAIN_SCEMA);
//        assertFalse(validator.shaclModelTargetClassesAreInDomainOfRespectiveProperties());
//        assertFalse(validator.checkModel());
//    }
//
    private ShaclValidator initializeOntologyValidator()
        throws IOException {

        String ontologyString = IoUtils.resourceAsString(
            Paths.get(RESOURCES_PATH, ENTITY_ONTOLOGY_TTL));

        return new ShaclValidator(ontologyString, Lang.TURTLE);
    }


    private Model parseModel(String fileName) throws IOException {
        String modelString=IoUtils.resourceAsString(Paths.get(RESOURCES_PATH,fileName));
        return parseModel(modelString,Lang.TURTLE);

    }
}
