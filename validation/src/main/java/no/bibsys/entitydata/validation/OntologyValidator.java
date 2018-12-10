package no.bibsys.entitydata.validation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyValidator implements ModelParser {

    private final static Logger logger = LoggerFactory.getLogger(OntologyValidator.class);

    private final Model ontology;

    public OntologyValidator(String ontologyString, Lang lang) {
        this.ontology = parseModel(ontologyString, lang);
    }

    public Model getOntology() {
        return ontology;
    }


    public boolean checkModel(Model model) {
        return shaclModelTargetClassesAreSubjectsOfOntology(model);

    }


    @VisibleForTesting
    public Set<Resource> listAllowedProperties() {
        return ontology.listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS).toSet();
    }


    public void actualPropertiesWithType(Model model) {
        List<Model> propertiesModels = listActualProperties(model);
        propertiesModels.stream()

    }


    private void pathAndDatatype(Model model) {
        List<RDFNode> pathObject = model.listObjectsOfProperty(ShaclConstants.PATH).toList();
        Preconditions.checkArgument(pathObject.size() == 1,
            "Only one sh:path object is allowed per property model");
        List<RDFNode> typeObject = model.listObjectsOfProperty(ShaclConstants.DATATYPE).toList();
        Preconditions.checkArgument(pathObject.size() == 1,
            "Only one sh:datatype object is allowed per property model");
    }

    public Map<Resource, RDFNode> allowedPropertiesWithRange() {
        return ontology
            .listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS)
            .toList().stream()
            .flatMap(resource -> ontology.listStatements(resource, RDFS.range, null, null)
                .toList().stream())
            .map(this::subjectObjectEntry)
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    }

    private SimpleEntry<Resource, RDFNode> subjectObjectEntry(Statement statement) {
        Resource subject = statement.getSubject();
        RDFNode object = statement.getObject();
        return new HashMap.SimpleEntry<>(subject, object);

    }


    public List<Model> listActualProperties(Model model) {
        List<RDFNode> properties = model.listObjectsOfProperty(ShaclConstants.PROPERTY).toList();
        return properties.stream().map(rdfNode -> (ResourceImpl) rdfNode)
            .map(resource -> resource.listProperties().toModel()).collect(Collectors.toList());

    }


    private boolean shaclModelTargetClassesAreSubjectsOfOntology(Model model) {
        Set<Resource> subjects = ontology.listSubjects().toSet();
        Set<Resource> objects = resourceObjectNodes(model, ShaclConstants.TARGETCLASS_PROPERTY);
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
                logger.warn("Invalid resources:{}", informOnWrongResource(superSet, subset));
                return false;
            }

        }

    }

    private String informOnWrongResource(Set<Resource> superSet, Set<Resource> subset) {
        Set<Resource> diff = calculateDiff(superSet, subset);
        Set<String> diffStrings = diff.stream().map(resource -> resource.toString()).collect(
            Collectors.toSet());
        String wrongResources = String.join(",", diffStrings);
        return wrongResources;


    }

    private Set<Resource> calculateDiff(Set<Resource> superSet, Set<Resource> subset) {
        Set<Resource> diff = new HashSet<>();
        diff.addAll(subset);
        diff.removeAll(superSet);
        return diff;
    }

    private Set<Resource> resourceObjectNodes(Model model, Property targetClassProperty) {
        List<RDFNode> objectNodes = model
            .listObjectsOfProperty(targetClassProperty).toList();
        if (areRDFNodesResources(objectNodes)) {
            return objectNodes.stream().map(rdfNode -> (Resource) rdfNode)
                .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }


    private boolean areRDFNodesResources(List<RDFNode> rdfNodes) {
        for (RDFNode node : rdfNodes) {
            if (!node.isResource()) {
                return false;
            }
        }
        return true;
    }


}
