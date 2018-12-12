package no.bibsys.entitydata.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import no.bibsys.utils.IoUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;


public class ShaclParserTest implements ModelParser {


    private final transient ShaclParser shaclParser;

    public ShaclParserTest() throws IOException {
        String shaclModelString = IoUtils
            .resourceAsString(Paths.get("validation", "validShaclValidationSchema.ttl"));
        Model model = parseModel(shaclModelString, Lang.TURTLE);
        this.shaclParser = new ShaclParser(model);
    }


    @Test
    public void listProperties_shaclModel_listOfBlankNodeModels() {
        Set<Resource> properties = shaclParser.listPropertyNames();
        assertThat(properties.size(), is(equalTo(1)));
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
            .listObjectsOfProperty(ShaclConstants.PATH)
            .toSet().stream().map(node -> (Resource) node).collect(Collectors.toSet());

        assertThat(propertyUris, is(equalTo(expectedPropertyUris)));
    }


}
