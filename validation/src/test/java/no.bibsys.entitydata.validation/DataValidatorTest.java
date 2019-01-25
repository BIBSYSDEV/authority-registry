package no.bibsys.entitydata.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import no.bibsys.entitydata.validation.exceptions.EntryFailedShaclValidationException;
import no.bibsys.utils.IoUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

public class DataValidatorTest extends ModelParser {

    public static final String VALIDATION_SCHEMA_TTL = "validShaclValidationSchema.ttl";
    public static final String RESOURCES_FOLDER = "validation";
    public static final String SHACL_VALIDATION_SCHEMA_TTL = "validShaclValidationSchema.ttl";
    public static final String VALID_GRAPH_JSON = "validGraph.json";
    public static final String INVALID_GRAPH_TTL = "invalidGraph.ttl";
    public static final String INVALID_GRAPH_JSON = "invalidGraph.json";
    private static final Property SH_CONFORMS = ResourceFactory
        .createProperty("http://www.w3.org/ns/shacl#conforms");
    private static final Property SH_VALIDATION_REPORT_CLASS = ResourceFactory
        .createProperty("http://www.w3.org/ns/shacl#ValidationReport");
    public static final String VALID_GRAPH_TTL = "validGraph.ttl";

    @Test
    public void validationResult_validationSchemaAndValidGraph_true() throws IOException {
        TestData testData = new TestData(Paths.get("validation", "validGraph.ttl")).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();
        DataValidator dataValidator = new DataValidator(validationModel);
        assertTrue(dataValidator.validationResult(dataModel));
    }

    @Test
    public void validationResult_validationSchemaAndInvalidGraph_false() throws IOException {
        TestData testData = new TestData(Paths.get("validation", "invalidGraph.ttl")).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();
        DataValidator dataValidator = new DataValidator(validationModel);
        assertFalse(dataValidator.validationResult(dataModel));
    }

    @Test
    public void valiationReport_validSchemaAndValidGraphTTL_report() throws IOException {
        TestData testData = new TestData(Paths.get("validation", VALID_GRAPH_TTL)).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();
        DataValidator dataValidator = new DataValidator(validationModel);
        Model report = dataValidator.validationReport(dataModel);
        Model expectedModel = ModelFactory.createDefaultModel();
        Resource blankNode = expectedModel.createResource();
        expectedModel
            .add(expectedModel.createStatement(blankNode, RDF.type, SH_VALIDATION_REPORT_CLASS));
        expectedModel.add(expectedModel.createStatement(blankNode, SH_CONFORMS,
            expectedModel.createTypedLiteral("true", XSDDatatype.XSDboolean)));
        assertTrue(expectedModel.isIsomorphicWith(report));
    }


    @Test
    public void valiationReport_validSchemaAndValidGraphJSONLD_report() throws IOException {
        Model validationModel = parseModel(
            IoUtils.resourceAsString(Paths.get(RESOURCES_FOLDER, SHACL_VALIDATION_SCHEMA_TTL)),
            Lang.TTL);
        Model dataModel = parseModel(
            IoUtils.resourceAsString(Paths.get(RESOURCES_FOLDER, VALID_GRAPH_JSON)), Lang.JSONLD);
        DataValidator dataValidator = new DataValidator(validationModel);
        Model report = dataValidator.validationReport(dataModel);
        Model expectedModel = ModelFactory.createDefaultModel();
        Resource blankNode = expectedModel.createResource();
        expectedModel
            .add(expectedModel.createStatement(blankNode, RDF.type, SH_VALIDATION_REPORT_CLASS));
        expectedModel.add(expectedModel.createStatement(blankNode, SH_CONFORMS,
            expectedModel.createTypedLiteral("true", XSDDatatype.XSDboolean)));
        assertTrue(expectedModel.isIsomorphicWith(report));
    }


    @Test(expected = EntryFailedShaclValidationException.class)
    public void isValidEntry_validationSchemaAndInvalidGraph_f()
        throws EntryFailedShaclValidationException, IOException {
        TestData testData = new TestData(Paths.get("validation", INVALID_GRAPH_TTL)).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();
        DataValidator dataValidator = new DataValidator(validationModel);
        assertFalse(dataValidator.isValidEntry(dataModel));
    }


    @Test(expected = EntryFailedShaclValidationException.class)
    public void isValidEntry_validationSchemaAndInvalidGraph_exception()
        throws EntryFailedShaclValidationException, IOException {
        Model validationModel = parseModel(
            IoUtils.resourceAsString(Paths.get(RESOURCES_FOLDER, SHACL_VALIDATION_SCHEMA_TTL)),
            Lang.TURTLE);

        Model dataModel = parseModel(
            IoUtils.resourceAsString(Paths.get(RESOURCES_FOLDER, INVALID_GRAPH_JSON)),
            Lang.JSONLD);
        DataValidator dataValidator = new DataValidator(validationModel);
        assertFalse(dataValidator.isValidEntry(dataModel));
    }


    private class TestData extends ModelParser {

        private final transient Path dataModelPath;
        private Model validationModel;
        private Model dataModel;

        public TestData(Path dataModelPath) {
            this.dataModelPath = dataModelPath;
        }

        public Model getValidationModel() {
            return validationModel;
        }

        public Model getDataModel() {
            return dataModel;
        }

        public TestData invoke() throws IOException {
            String validationModelString = IoUtils
                .resourceAsString(Paths.get(RESOURCES_FOLDER, VALIDATION_SCHEMA_TTL));
            validationModel = parseModel(validationModelString, Lang.TURTLE);

            String dataStream = IoUtils.resourceAsString(dataModelPath);
            dataModel = parseModel(dataStream, Lang.TURTLE);
            return this;
        }
    }
}
