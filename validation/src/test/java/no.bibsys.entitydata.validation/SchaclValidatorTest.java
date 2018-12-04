package no.bibsys.entitydata.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import no.bibsys.aws.tools.IoUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

public class SchaclValidatorTest {

    private static final Property SH_CONFORMS = ResourceFactory
        .createProperty("http://www.w3.org/ns/shacl#conforms");
    private static final Property SH_VALIDATION_REPORT_CLASS = ResourceFactory
        .createProperty("http://www.w3.org/ns/shacl#ValidationReport");


    @Test
    public void validationResult_validationSchemaAndValidGraph_true() {
        TestData testData = new TestData(Paths.get("validation", "validGraph.ttl")).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();
        SchaclValidator schaclValidator = new SchaclValidator(validationModel);
        assertTrue(schaclValidator.validationResult(dataModel));


    }

    @Test
    public void validationResult_validationSchemaAndInvalidGraph_false() {
        TestData testData = new TestData(Paths.get("validation", "invalidGraph.ttl")).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();

        SchaclValidator schaclValidator = new SchaclValidator(validationModel);

        assertFalse(schaclValidator.validationResult(dataModel));


    }


    @Test
    public void valiationReport_validSchemaAndValidGraph_report() {
        TestData testData = new TestData(Paths.get("validation", "validGraph.ttl")).invoke();
        Model validationModel = testData.getValidationModel();
        Model dataModel = testData.getDataModel();

        SchaclValidator schaclValidator = new SchaclValidator(validationModel);

        Model report = schaclValidator.validationReport(dataModel);

        Model expectedModel = ModelFactory.createDefaultModel();
        Resource blankNode = expectedModel.createResource();
        expectedModel.add(expectedModel.createStatement(blankNode,
            RDF.type, SH_VALIDATION_REPORT_CLASS));
        expectedModel.add(expectedModel.createStatement(blankNode,
            SH_CONFORMS,
            expectedModel.createTypedLiteral("true", XSDDatatype.XSDboolean)));

        assertTrue(expectedModel.isIsomorphicWith(report));


    }


    private class TestData {

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

        public TestData invoke() {
            validationModel = ModelFactory.createDefaultModel();
            dataModel = ModelFactory.createDefaultModel();
            InputStream validationSchemaStream = IoUtils
                .inputStreamFromResources(Paths.get("validation", "validationSchema.ttl"));
            RDFDataMgr.read(validationModel, validationSchemaStream, Lang.TURTLE);

            InputStream dataStream = IoUtils
                .inputStreamFromResources(dataModelPath);
            RDFDataMgr.read(dataModel, dataStream, Lang.TURTLE);
            return this;
        }
    }
}
