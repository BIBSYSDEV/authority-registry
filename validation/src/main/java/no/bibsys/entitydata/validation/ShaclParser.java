package no.bibsys.entitydata.validation;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.rdfutils.RdfConstants;
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

    public static final String DOMAIN_MODEL_QUERY_TTL = "shaclGenerateDomainModelQuery.sparql";
    public static final String RANGE_MODEL_QUERY_TTL = "shaclGenerateRangeModelQuery.sparql";
    public static final String RESOURCES_FOLDER = "validation";
    private final transient Model model;

    public ShaclParser(Model shaclModel) {
        this.model = shaclModel;
    }

    public Set<Resource> listPropertyNames() {

        Set<Model> propertiesModels = listProperties();
        return propertiesModels.stream().flatMap(m -> m.listObjectsOfProperty(ShaclConstants.PATH).toSet().stream())
            .map(rdfNode -> (Resource) rdfNode).filter(RdfConstants::isNotRdfType).collect(Collectors.toSet());
    }

    private Set<Model> listProperties() {
        List<RDFNode> properties = model.listObjectsOfProperty(ShaclConstants.PROPERTY).toList();
        return properties.stream().map(rdfNode -> (ResourceImpl) rdfNode)
            .map(resource -> resource.listProperties().toModel()).collect(Collectors.toSet());

    }

    public Set<Resource> resourceObjectNodes(Property targetClassProperty) {
        List<RDFNode> objectNodes = model.listObjectsOfProperty(targetClassProperty).toList();
        if (areRDFNodesResources(objectNodes)) {
            return objectNodes.stream().map(rdfNode -> (Resource) rdfNode).collect(Collectors.toSet());
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
        return generateModel(DOMAIN_MODEL_QUERY_TTL);
    }

    private Model generateModel(String rangeModelQueryTtl) throws IOException {
        String queryString = IoUtils.resourceAsString(Paths.get(RESOURCES_FOLDER, rangeModelQueryTtl));
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        Model resultModel = qe.execConstruct();
        return resultModel;
    }

    public Model generateRangeModel() throws IOException {
        return generateModel(RANGE_MODEL_QUERY_TTL);
    }

}
