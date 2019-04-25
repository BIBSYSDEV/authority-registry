package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.exceptions.TargetClassPropertyObjectIsNotAResourceException;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import no.bibsys.utils.ModelParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShaclValidator extends ModelParser {

    private static final String TargetClassPropertyCheckErrorMessage =
            ShaclConstants.TARGETCLASS_PROPERTY.getLocalName() + " property object is not a resource: ";
    private static final String TARGET_CLASS_PROPERTY_CHECK_ERROR_MESSAGE = TargetClassPropertyCheckErrorMessage;
    private final transient OntologyParser ontologyParser;

    public ShaclValidator(String ontologyString, Lang ontolgyLang) {
        super();
        Model ontology = parseModel(ontologyString, ontolgyLang);
        this.ontologyParser = new OntologyParser(ontology);

    }

    public Model getOntology() {
        return ontologyParser.getOntology();
    }

    public boolean checkModel(Model shaclModel)
            throws IOException, ShaclModelValidationException, TargetClassPropertyObjectIsNotAResourceException {
        ShaclParser shaclParser = new ShaclParser(shaclModel);
        return shaclModelTargetClassesAreClassesOfOntology(shaclModel)
               && shaclModelPathObjectsAreOntologyProperties(shaclParser)
               && shaclModelDatatypeObjectsMapExactlyPropertyRange(shaclParser)
               && shaclModelTargetClassesAreInDomainOfRespectiveProperties(shaclParser);
    }

    private boolean shaclModelTargetClassesAreClassesOfOntology(Model shaclValidationModel)
            throws ShaclModelTargetClassesAreNotClassesOfOntologyException,
            TargetClassPropertyObjectIsNotAResourceException {
        Set<Resource> subjects = ontologyParser.listSubjects(RDF.type, RDFS.Class);
        Set<Resource> objects = resourceObjectNodes(shaclValidationModel);
        if (subjects.containsAll(objects)) {
            return true;
        } else {
            throw new ShaclModelTargetClassesAreNotClassesOfOntologyException();
        }
    }

    private Set<Resource> resourceObjectNodes(Model shaclValidationModel)
            throws TargetClassPropertyObjectIsNotAResourceException {
        List<RDFNode> objectNodes =
                shaclValidationModel.listObjectsOfProperty(ShaclConstants.TARGETCLASS_PROPERTY).toList();
        checkThatRdfNodesResourcesOrThrowException(objectNodes);
        return objectNodes.stream().map(rdfNode -> (Resource) rdfNode).collect(Collectors.toSet());
    }

    private void checkThatRdfNodesResourcesOrThrowException(List<RDFNode> rdfNodes)
            throws TargetClassPropertyObjectIsNotAResourceException {
        for (RDFNode node : rdfNodes) {
            if (!node.isResource()) {
                throw new TargetClassPropertyObjectIsNotAResourceException(
                        TARGET_CLASS_PROPERTY_CHECK_ERROR_MESSAGE + node.toString());
            }
        }
    }

    private boolean shaclModelPathObjectsAreOntologyProperties(ShaclParser shaclParser)
            throws ShaclModelPathObjectsAreNotOntologyPropertiesException {
        Set<Resource> allowedProperties = ontologyParser.listProperties();
        Set<Resource> actualProperties = shaclParser.listPropertyNames();
        if (allowedProperties.containsAll(actualProperties)) {
            return true;
        } else {
            throw new ShaclModelPathObjectsAreNotOntologyPropertiesException();
        }
    }

    private boolean shaclModelDatatypeObjectsMapExactlyPropertyRange(ShaclParser shaclParser)
            throws IOException, ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException {
        Model actualRanges = shaclParser.generateRangeModel();
        Model validRanges = ontologyParser.propertiesWithRange();
        if (validRanges.containsAll(actualRanges)) {
            return true;
        } else {
            throw new ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException();
        }
    }

    private boolean shaclModelTargetClassesAreInDomainOfRespectiveProperties(ShaclParser shaclParser)
            throws IOException, ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException {
        Model ontologyDomains =
                ontologyParser.getOntology().listStatements(null, RDFS.domain, (RDFNode) null).toModel();
        Model shaclDomains = shaclParser.generateDomainModel();
        if (ontologyDomains.containsAll(shaclDomains)) {
            return true;
        } else {
            throw new ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException();
        }
    }
}
