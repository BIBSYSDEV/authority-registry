package no.bibsys.entitydata.validation;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import no.bibsys.utils.IoUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class ShaclParser {

    public static final String SPACE = " ";
    public static final String NEWLINE = String.format("%n");
    private final transient Model model;

    public ShaclParser(Model shaclModel) {
        this.model = shaclModel;
    }

    public Set<Resource> listPropertyNames() {
        Set<Model> propertiesModels = listProperties();
        Set<Resource> properties = propertiesModels.stream()
            .flatMap(model ->
                model.listObjectsOfProperty(ShaclConstants.PATH).toSet().stream())
            .map(rdfNode -> (Resource) rdfNode).collect(Collectors.toSet());

        return properties;
    }


    private Set<Model> listProperties() {
        List<RDFNode> properties = model.listObjectsOfProperty(ShaclConstants.PROPERTY)
            .toList();
        return properties.stream().map(rdfNode -> (ResourceImpl) rdfNode)
            .map(resource -> resource.listProperties().toModel()).collect(Collectors.toSet());

    }


    public Map<Resource, Resource> propertiesWithRange() {
        Set<Model> propertiesModels = listProperties();
        return propertiesModels.stream().map(this::pathAndDatatype)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    }


    private Entry<Resource, Resource> pathAndDatatype(Model blankNodeModel) {
        List<RDFNode> propertyNameList = blankNodeModel
            .listObjectsOfProperty(ShaclConstants.PATH)
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


    public Set<Resource> resourceObjectNodes(Property targetClassProperty) {
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


    public Model getModel() {
        return this.model;
    }


    public Model generateDomainModel() throws IOException {

        String queryString = IoUtils
            .resourceAsString(Paths.get("validation", "shaclGenerateDomainModelQuery.ttl"));
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        Model resultModel = qe.execConstruct();
        return resultModel;
    }


}
