package no.bibsys.entitydata.validation;

import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ModelParserTest extends ModelParser {

    private static final String VALIDATION_FOLDER = "validation";
    private static final String VALID_GRAPH_JSON = "validGraph.json";
    private static final String SHACL_VALIDATION_SCHEMA_TTL = "validShaclValidationSchema.ttl";
    private static final String VALID_GRAPH_TTL = "validGraph.ttl";
    private static final String INVALID_INPUT = "InvalidInput";

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
    public void getObjects_invalidInput_throwsException() {
        Model model = parseModel(INVALID_INPUT, Lang.TURTLE);
        Set<Resource> objects = getUriResourceObjects(model);
        Set<RDFNode> resources = model.listObjects().toSet()
            .stream()
            .filter(RDFNode::isURIResource)
            .collect(Collectors.toSet());
        int expectedNumberOfObjects = resources.size();
        assertThat(objects.size(), is(equalTo(expectedNumberOfObjects)));
    }

    @Test
    public void testParseModelWithRoundtrip() throws IOException {
        String inputString = IoUtils
            .resourceAsString(Paths.get(VALIDATION_FOLDER, VALID_GRAPH_TTL));
        Model model = parseModel(inputString, Lang.TURTLE);
        String jsonld = writeData(model, Lang.JSONLD, null);
        assertTrue(parseModel(jsonld, Lang.JSONLD).isIsomorphicWith(model));
        String ttl = writeData(model, Lang.TURTLE, null);
        assertTrue(parseModel(ttl, Lang.TURTLE).isIsomorphicWith(model));
        String json = writeData(model, Lang.RDFJSON, null);
        assertTrue(parseModel(json, Lang.RDFJSON).isIsomorphicWith(model));
        String ntriples = writeData(model, Lang.NTRIPLES, null);
        assertTrue(parseModel(ntriples, Lang.NTRIPLES).isIsomorphicWith(model));
        String rdfxml = writeData(model, Lang.RDFXML, null);
        assertTrue(parseModel(rdfxml, Lang.RDFXML).isIsomorphicWith(model));
    }
}
