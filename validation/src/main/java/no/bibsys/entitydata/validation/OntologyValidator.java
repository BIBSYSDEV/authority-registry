package no.bibsys.entitydata.validation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        boolean result = shaclModelTargetClassesAreSubjectsOfOntology(model);
        if (result) {
            result=allActualPropertiesAreIncludedinOntology(model);

        }

        return  result;

    }

    public boolean allActualPropertiesAreIncludedinOntology(Model model) {

        Set<Resource> allowedPropeties = listAllowedProperties();
        Set<Resource> actualPropeties = actualPropertiesOnlyNames(model);
        actualPropeties.removeAll(allowedPropeties);
        return actualPropeties.isEmpty();
    }


    @VisibleForTesting
    public Set<Resource> listAllowedProperties() {
        return ontology.listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS).toSet();
    }


    public Map<Resource, Resource> actualPropertiesWithRange(Model shaclModel) {
        Set<Model> propertiesModels = listActualProperties(shaclModel);
        return propertiesModels.stream().map(this::pathAndDatatype)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    }


    private Set<Resource> actualPropertiesOnlyNames(Model shaclModel) {
        Set<Model> propertiesModels = listActualProperties(shaclModel);
        Set<Resource> properties = propertiesModels.stream()
            .flatMap(model ->
                model.listObjectsOfProperty(ShaclConstants.PATH).toSet().stream())
            .map(rdfNode -> (Resource) rdfNode).collect(Collectors.toSet());

        return properties;
    }


    public Map<Resource, RDFNode> allowedPropertiesWithRange() {
        return ontology
            .listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS)
            .toList().stream()
            .flatMap(resource -> ontology.listStatements(resource, RDFS.range, null, null)
                .toList().stream())
            .map(this::subjectAndObjectFromStatement)
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    }


    private Entry<Resource, Resource> pathAndDatatype(Model blankNodeModel) {
        List<RDFNode> propertyNameList = blankNodeModel.listObjectsOfProperty(ShaclConstants.PATH)
            .toList();
        Preconditions.checkArgument(propertyNameList.size() == 1,
            "Only one sh:path object is allowed per property model");
        List<RDFNode> propertyRangeList = blankNodeModel
            .listObjectsOfProperty(ShaclConstants.DATATYPE).toList();
        Preconditions.checkArgument(propertyRangeList.size() == 1,
            "Only one sh:datatype object is allowed per property model");
        Resource propertyName = (Resource) propertyNameList.iterator().next();
        Resource propertyRange = (Resource) propertyRangeList.iterator().next();
        return new HashMap.SimpleEntry<>(propertyName, propertyRange);


    }


    private SimpleEntry<Resource, Resource> subjectAndObjectFromStatement(Statement statement) {
        Resource subject = statement.getSubject();
        Resource object = (Resource) statement.getObject();
        return new HashMap.SimpleEntry<Resource, Resource>(subject, object);

    }


    private Set<Model> listActualProperties(Model model) {
        List<RDFNode> properties = model.listObjectsOfProperty(ShaclConstants.PROPERTY).toList();
        return properties.stream().map(rdfNode -> (ResourceImpl) rdfNode)
            .map(resource -> resource.listProperties().toModel()).collect(Collectors.toSet());

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
