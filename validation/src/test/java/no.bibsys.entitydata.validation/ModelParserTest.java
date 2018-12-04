package no.bibsys.entitydata.validation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

public class ModelParserTest implements ModelParser{


    @Test
    public void parseJson_jsonLdString_model() throws IOException {
        String modelString= IoUtils.resourceAsString(Paths.get("validation","validGraph.json"));
        Model actual= parseModel(modelString, Lang.JSONLD);

        Model expected= ModelFactory.createDefaultModel();
        Resource subject= expected.createResource("http://example.org/a");
        Resource classObject= expected.createResource("http://example.org/ClassA");

        Property nameProperty= expected.createProperty("http://example.org/name");

        expected.add(expected.createStatement(subject, RDF.type,classObject))
            .add(expected.createStatement(subject,nameProperty,"entityA"));

        assertTrue(actual.isIsomorphicWith(expected));

    }


    @Test
    public void getObjects_model_allObjectsThatAreIRIsOrLiterals() throws IOException {
        String modelString=IoUtils.resourceAsString(Paths.get("validation","validationSchema.ttl"));
        Model model=parseModel(modelString,Lang.TURTLE);
        List<Resource> objects = getObjects(model);

        int expectedNumberOfObjects=4;
        assertThat(objects.size(),is(equalTo(expectedNumberOfObjects)));

    }



}
