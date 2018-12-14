package no.bibsys.entitydata.validation;

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

    public Set<Resource> listSubjects(Property property, Resource object) {
        return ontology.listResourcesWithProperty(property, object).toSet();
    }

    public Model getOntology() {
        return this.ontology;
    }

    public Model propertiesWithRange() {
        return ontology.listStatements(null, RDFS.range, (RDFNode) null).toModel();
    }

    public Set<Resource> getPropertyDomain(Resource subject) {
        return ontology.listStatements(subject, RDFS.domain, (RDFNode) null)
            .toList().stream()
            .map(Statement::getObject)
            .map(rdfNode -> (Resource) rdfNode)
            .collect(Collectors.toSet());
    }
}
