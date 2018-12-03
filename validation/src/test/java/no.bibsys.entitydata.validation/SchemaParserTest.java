package no.bibsys.entitydata.validation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

public class SchemaParserTest {


    @Test
    public void parseJson_jsonLdString_model() throws IOException {
        String modelString= IoUtils.resourceAsString(Paths.get("validation","validGraph.json"));
        SchemaParser schemaParser=new SchemaParser() {};
        Model actual=schemaParser.parseJsonLD(modelString);

        Model expected= ModelFactory.createDefaultModel();
        Resource subject= expected.createResource("http://example.org/a");
        Resource classObject= expected.createResource("http://example.org/ClassA");

        Property nameProperty= expected.createProperty("http://example.org/name");

        expected.add(expected.createStatement(subject, RDF.type,classObject))
            .add(expected.createStatement(subject,nameProperty,"entityA"));

        assertTrue(actual.isIsomorphicWith(expected));



    }



}
