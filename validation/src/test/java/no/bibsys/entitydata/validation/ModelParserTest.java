package no.bibsys.entitydata.validation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.junit.Ignore;
import org.junit.Test;

import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;

public class ModelParserTest extends ModelParser {

    public static final String VALIDATION_FOLDER = "validation";
    public static final String VALID_GRAPH_JSON = "validGraph.json";
    public static final String SHACL_VALIDATION_SCHEMA_TTL = "validShaclValidationSchema.ttl";

    @Test
    public void parseJson_jsonLdString_model() throws IOException {
        String modelString = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_GRAPH_JSON));
        Model actual = parseModel(modelString, Lang.JSONLD);
        assertFalse(actual.isEmpty());
    }

    @Test
    public void getObjects_model_allObjectsThatAreIRIsOrLiterals() throws IOException {
        String modelString = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, SHACL_VALIDATION_SCHEMA_TTL));
        Model model = parseModel(modelString, Lang.TURTLE);
        Set<Resource> objects = getUriResourceObjects(model);
        Set<RDFNode> resources = model.listObjects().toSet()
            .stream()
            .filter(RDFNode::isURIResource)
            .collect(Collectors.toSet());
        int expectedNumberOfObjects = resources.size();
        assertThat(objects.size(), is(equalTo(expectedNumberOfObjects)));
    }



    @Test(expected = ValidationSchemaSyntaxErrorException.class)
    public void getObjects_invalidInput_throwsException() throws IOException {
        Model model = parseModel("InvalidInput", Lang.TURTLE);
        Set<Resource> objects = getUriResourceObjects(model);
        Set<RDFNode> resources = model.listObjects().toSet()
            .stream()
            .filter(RDFNode::isURIResource)
            .collect(Collectors.toSet());
        int expectedNumberOfObjects = resources.size();
        assertThat(objects.size(), is(equalTo(expectedNumberOfObjects)));
    }


    @Ignore
    @Test
    public void foo() throws IOException {
        String inputString = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, "validGraph.ttl"));
        Model model = parseModel(inputString, Lang.TURTLE);
        String jsonls = writeData(model, Lang.JSONLD);
        String ttl = writeData(model, Lang.TURTLE);
        String json = writeData(model, Lang.RDFJSON);
        String nquads = writeData(model, Lang.NTRIPLES);
        String rdfxml = writeData(model, Lang.RDFXML);
        String rdf = writeData(model, Lang.RDFTHRIFT);
        assertFalse(1 == 2);
    }
}
