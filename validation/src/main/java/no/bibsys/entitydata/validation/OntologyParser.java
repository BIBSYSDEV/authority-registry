package no.bibsys.entitydata.validation;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import no.bibsys.entitydata.validation.rdfutils.RdfsConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class OntologyParser {


    private final transient Model ontology;


    public OntologyParser(Model ontology) {
        this.ontology = ontology;
    }


    public Set<Resource> listProperties() {
        return ontology.listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS).toSet();
    }


    public Map<Resource, Resource> propertiesWithRange() {
        return ontology
            .listResourcesWithProperty(RDF.type, RdfsConstants.PROPERTY_CLASS)
            .toList().stream()
            .flatMap(resource -> ontology.listStatements(resource, RDFS.range, (RDFNode) null)
                .toList().stream())
            .map(this::subjectAndObjectFromStatement)
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    }


    private SimpleEntry<Resource, Resource> subjectAndObjectFromStatement(Statement statement) {
        Resource subject = statement.getSubject();
        Resource object = (Resource) statement.getObject();
        return new HashMap.SimpleEntry<>(subject, object);

    }

    public Set<Resource> listSubjects() {
        return ontology.listSubjects().toSet();
    }


    public Set<Resource> listSubjects(Property property, Resource object) {
        return ontology.listResourcesWithProperty(property, object).toSet();
    }


    public Model getOntology() {
        return this.ontology;

    }

    public Set<Resource> getPropertyDomain(Resource propertySubject) {
        return ontology.listStatements(propertySubject, RDFS.domain, (RDFNode) null)
            .toList().stream()
            .map(Statement::getObject)
            .map(rdfNode -> (Resource) rdfNode)
            .collect(Collectors.toSet());
    }
}
