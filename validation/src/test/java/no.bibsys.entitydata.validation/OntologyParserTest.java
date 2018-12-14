package no.bibsys.entitydata.validation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import no.bibsys.entitydata.validation.rdfutils.RdfsConstants;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

public class OntologyParserTest extends ModelParser {

    private static final String RESOURCE_FOLDER = "validation";
    private static final String ENTITY_ONTOLOGY_TTL = "unit-entity-ontology.ttl";
    private final transient OntologyParser ontologyParser;

    public OntologyParserTest() throws IOException {
        String ontologyString = IoUtils.resourceAsString(
            Paths.get(RESOURCE_FOLDER, ENTITY_ONTOLOGY_TTL));
        Model ontology = loadData(ontologyString, Lang.TURTLE);
        this.ontologyParser = new OntologyParser(ontology);
    }

    @Test
    public void listProperties_ontology_allSubjectsWithTypeProperty() {
        Set<Resource> properties = ontologyParser
            .listProperties();
        Set<Resource> expectedProperties = ontologyParser.getOntology()
            .listResourcesWithProperty(RDF.type,
                RdfsConstants.PROPERTY_CLASS).toSet();
        assertThat(properties, is(equalTo(expectedProperties)));
    }

    @Test
    public void listSubjectsWithPropertyAndObject_PropertyAndObject_allSubjectsWithThePropertyAndTheObject() {
        Set<Resource> expectedSubjects = ontologyParser.getOntology()
            .listResourcesWithProperty(RDF.type, RDFS.Class).toSet();

        Property property = RDF.type;
        Resource object = RDFS.Class;
        Set<Resource> subjects = ontologyParser.listSubjects(property, object);
        assertThat(subjects, is(equalTo(expectedSubjects)));
    }

    @Test
    public void propertyDomain_property_setOfResources() {
        Model domainStatements = ontologyParser.getOntology()
            .listStatements(null, RDFS.domain, (RDFNode) null).toModel();

        for (Statement domainStatement : domainStatements.listStatements().toSet()) {
            Resource propertySubject = domainStatement.getSubject();
            Set<Resource> propertyDomain = ontologyParser.getPropertyDomain(propertySubject);
            Resource resourceInDomain = (Resource) domainStatement.getObject();
            assertTrue(propertyDomain.contains(resourceInDomain));
        }
    }
}
