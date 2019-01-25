package no.bibsys.entitydata.validation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.exceptions.ValidationSchemaSyntaxErrorException;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.junit.Test;

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
}
