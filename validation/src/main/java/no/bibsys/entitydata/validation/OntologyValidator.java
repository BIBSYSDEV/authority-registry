package no.bibsys.entitydata.validation;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Set;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class OntologyValidator implements ModelParser {


    private final transient OntologyParser ontologyParser;
    private final transient ShaclParser shaclParser;

    public OntologyValidator(String ontologyString, Lang ontolgyLang,
        String shaclModelString, Lang shaclModelLang) {
        Model ontology = parseModel(ontologyString, ontolgyLang);
        Model shaclModel = parseModel(shaclModelString, shaclModelLang);
        this.ontologyParser = new OntologyParser(ontology);
        this.shaclParser = new ShaclParser(shaclModel);

    }


    public Model getOntology() {
        return ontologyParser.getOntology();
    }


    public boolean checkModel() throws IOException {

        return shaclModelPropertiesAreIncludedInOntology()
            && shaclModelTargetClassesAreClassesOfOntology()
            && shaclModelPathObjectsAreOntologyProperties()
            && shaclModelDatatypeObjectsMapExactlyPropertyRange()
            && shaclModelTargetClassesAreInDomainOfRespectiveProperties();


    }

    public boolean shaclModelPropertiesAreIncludedInOntology() {

        Set<Resource> allowedPropeties = ontologyParser.listProperties();
        Set<Resource> actualPropeties = shaclParser.listPropertyNames();
        actualPropeties.removeAll(allowedPropeties);
        return actualPropeties.isEmpty();
    }


    @VisibleForTesting
    public boolean shaclModelTargetClassesAreClassesOfOntology() {
        Set<Resource> subjects = ontologyParser.listSubjects(RDF.type, RDFS.Class);
        Set<Resource> objects = shaclParser
            .resourceObjectNodes(ShaclConstants.TARGETCLASS_PROPERTY);
        return subjects.containsAll(objects);


    }


    public boolean shaclModelPathObjectsAreOntologyProperties() {
        Set<Resource> allowedProperties = ontologyParser.listProperties();
        Set<Resource> actualProperties = shaclParser.listPropertyNames();
        return allowedProperties.containsAll(actualProperties);


    }

    public boolean shaclModelDatatypeObjectsMapExactlyPropertyRange() throws IOException {

        Model actualRanges = shaclParser.generateRangeModel();
        Model validRanges = ontologyParser.propertiesWithRange();
        return validRanges.containsAll(actualRanges);

    }


    public boolean shaclModelTargetClassesAreInDomainOfRespectiveProperties() throws IOException {
        Model ontologyDomains = ontologyParser.getOntology()
            .listStatements(null, RDFS.domain, (RDFNode) null).toModel();

        Model shaclDomains = shaclParser.generateDomainModel();

        return ontologyDomains.containsAll(shaclDomains);
    }
}
