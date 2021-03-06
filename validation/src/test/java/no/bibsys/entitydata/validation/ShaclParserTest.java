package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.rdfutils.RdfConstants;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShaclParserTest extends ModelParser {

    private static final String TEST_RESOURCES_FOLDER = "testQueries";
    private static final String PROPERTIES_COUNT_QUERY = "propertiesInShaclModel.sparql";
    private static final String RESOURCES_FOLDER = "validation";
    private static final String VALID_SHACL_SCHEMA_TTL = "validShaclValidationSchema.ttl";
    private final transient ShaclParser shaclParser;

    public ShaclParserTest() throws IOException {
        String shaclModelString = IoUtils
            .resourceAsString(Paths.get(RESOURCES_FOLDER, VALID_SHACL_SCHEMA_TTL));
        Model model = parseModel(shaclModelString, Lang.TURTLE);
        this.shaclParser = new ShaclParser(model);
    }


    @Test
    public void listProperties_shaclModel_listOfBlankNodeModels() throws IOException {
        Set<Resource> properties = shaclParser.listPropertyNames();
        String queryString = IoUtils
            .resourceAsString(Paths.get(TEST_RESOURCES_FOLDER, PROPERTIES_COUNT_QUERY));

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, shaclParser.getModel());
        ResultSet queryRes = queryExecution.execSelect();
        Integer numberOfProperties = queryRes.getResultVars().stream()
            .map(var -> queryRes.next().getLiteral(var)).map(literal -> literal.getInt())
            .findAny().orElse(-1);

        assertThat(properties.size(), is(equalTo(numberOfProperties)));
    }

    @Test
    public void generateDomainStatements_shaclModel_pathAndTargetClassPairs() throws IOException {

        Model model = shaclParser.generateDomainModel();
        Model domainStatements = model.listStatements(null, RDFS.domain, (RDFNode) null).toModel();
        assertThat(model.size(), is(equalTo(domainStatements.size())));

        checkThatPropertyUrisAreSubjectsInGeneratedModel(domainStatements);
        checkThatTargetClassesAreObjectsInGeneratedModel(domainStatements);
    }



    private void checkThatTargetClassesAreObjectsInGeneratedModel(Model domainStatements) {
        Set<RDFNode> expectedDomainUris = shaclParser.getModel()
            .listObjectsOfProperty(ShaclConstants.TARGETCLASS_PROPERTY).toSet()
            .stream()
            .map(node -> (Resource) node)
            .collect(Collectors.toSet());
        Set<RDFNode> domainUris = domainStatements.listObjectsOfProperty(RDFS.domain).toSet();

        assertThat(domainUris, is(equalTo(expectedDomainUris)));
    }

    private void checkThatPropertyUrisAreSubjectsInGeneratedModel(Model domainStatements) {
        Set<Resource> propertyUris = domainStatements.listSubjects().toSet();
        Set<Resource> expectedPropertyUris = shaclParser.getModel()
            .listObjectsOfProperty(ShaclConstants.PATH).toSet().stream().map(node -> (Resource) node)
            .filter(RdfConstants::isNotRdfType)
            .collect(Collectors.toSet());

        assertThat(propertyUris, is(equalTo(expectedPropertyUris)));
    }



}
