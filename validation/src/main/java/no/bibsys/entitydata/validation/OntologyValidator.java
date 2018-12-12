package no.bibsys.entitydata.validation;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyValidator implements ModelParser {

    private static final String DELIMITER = ",";
    private static final Logger logger = LoggerFactory.getLogger(OntologyValidator.class);
    private final transient OntologyParser ontologyParser;
    private final transient ShaclParser shaclParser;

    private final transient Map<Resource, Resource> allowedRanges;

    public OntologyValidator(String ontologyString, Lang ontolgyLang,
        String shaclModelString, Lang shaclModelLang) {
        Model ontology = parseModel(ontologyString, ontolgyLang);
        Model shaclModel = parseModel(shaclModelString, shaclModelLang);
        this.ontologyParser = new OntologyParser(ontology);
        this.shaclParser = new ShaclParser(shaclModel);
        this.allowedRanges = ontologyParser.propertiesWithRange();
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
        return isSubset(subjects, objects);


    }

    private boolean isSubset(Set<Resource> superSet, Set<Resource> subset) {
        if (subset.isEmpty()) {
            logger.warn("Empty subset");
            return false;
        } else {
            if (superSet.containsAll(subset)) {
                return true;
            } else {
                logger.warn("Invalid resources:{}", showSetDiffForErrorMessage(superSet, subset));
                return false;
            }

        }

    }

    private String showSetDiffForErrorMessage(Set<Resource> superSet, Set<Resource> subset) {
        Set<Resource> diff = calculateDiff(superSet, subset);
        Set<String> diffStrings = diff.stream().map(resource -> resource.toString()).collect(
            Collectors.toSet());
        String wrongResources = String.join(DELIMITER, diffStrings);
        return wrongResources;


    }

    private Set<Resource> calculateDiff(Set<Resource> superSet, Set<Resource> subset) {
        Set<Resource> diff = new HashSet<>();
        diff.addAll(subset);
        diff.removeAll(superSet);
        return diff;
    }


    public boolean shaclModelPathObjectsAreOntologyProperties() {
        Set<Resource> allowedProperties = ontologyParser.listProperties();
        Set<Resource> actualProperties = shaclParser.listPropertyNames();
        return allowedProperties.containsAll(actualProperties);


    }

    public boolean shaclModelDatatypeObjectsMapExactlyPropertyRange() {

        Map<Resource, Resource> actualRanges = shaclParser.propertiesWithRange();

        Set<Entry<Resource, Resource>> validationSet = actualRanges.entrySet().stream()
            .filter(this::isNotValidRange)
            .collect(Collectors.toSet());

        return validationSet.isEmpty();


    }


    private boolean isNotValidRange(Entry<Resource, Resource> propertyRangeEntry) {
        Resource propertyUri = propertyRangeEntry.getKey();
        if (allowedRanges.containsKey(propertyUri)) {
            Resource actualRange = propertyRangeEntry.getValue();
            Resource allowedRange = allowedRanges.get(propertyUri);
            return !allowedRange.equals(actualRange);
        } else {
            return true;
        }

    }


    public boolean shaclModelTargetClassesAreInDomainOfRespectiveProperties() throws IOException {
        Model ontologyDomains = ontologyParser.getOntology()
            .listStatements(null, RDFS.domain, (RDFNode) null).toModel();

        Model shaclDomains = shaclParser.generateDomainModel();

        return ontologyDomains.containsAll(shaclDomains);
    }
}
