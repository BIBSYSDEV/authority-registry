package no.bibsys.entitydata.validation;

import java.io.IOException;
import java.util.Set;
import no.bibsys.entitydata.validation.exceptions.ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelPropertiesAreNotIcludedInOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotClassesOfOntologyException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException;
import no.bibsys.entitydata.validation.exceptions.ShaclModelValidationException;
import no.bibsys.entitydata.validation.rdfutils.ShaclConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class ShaclValidator extends ModelParser {

    private final transient OntologyParser ontologyParser;

    public ShaclValidator(String ontologyString, Lang ontolgyLang) {
        super();
        Model ontology = parseModel(ontologyString, ontolgyLang);
        this.ontologyParser = new OntologyParser(ontology);

    }

    public Model getOntology() {
        return ontologyParser.getOntology();
    }

    public boolean checkModel(Model shaclModel) throws IOException, ShaclModelValidationException {
        ShaclParser shaclParser = new ShaclParser(shaclModel);
        return shaclModelPropertiesAreIncludedInOntology(shaclParser) && shaclModelTargetClassesAreClassesOfOntology(
            shaclParser) && shaclModelPathObjectsAreOntologyProperties(shaclParser)
            && shaclModelDatatypeObjectsMapExactlyPropertyRange(shaclParser)
            && shaclModelTargetClassesAreInDomainOfRespectiveProperties(shaclParser);
    }


    private boolean shaclModelPropertiesAreIncludedInOntology(ShaclParser shaclParser)
        throws ShaclModelPropertiesAreNotIcludedInOntologyException {

        Set<Resource> allowedPropeties = ontologyParser.listProperties();
        Set<Resource> actualPropeties = shaclParser.listPropertyNames();
        actualPropeties.removeAll(allowedPropeties);
        if (actualPropeties.isEmpty()) {
            return true;
        } else {
            throw ShaclModelPropertiesAreNotIcludedInOntologyException.newException(actualPropeties);
        }
    }


    private boolean shaclModelTargetClassesAreClassesOfOntology(ShaclParser shaclParser)
        throws ShaclModelTargetClassesAreNotClassesOfOntologyException {
        Set<Resource> subjects = ontologyParser.listSubjects(RDF.type, RDFS.Class);
        Set<Resource> objects = shaclParser.resourceObjectNodes(ShaclConstants.TARGETCLASS_PROPERTY);
        if (subjects.containsAll(objects)) {
            return true;
        } else {
            throw new ShaclModelTargetClassesAreNotClassesOfOntologyException();
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
        Model ontologyDomains = ontologyParser.getOntology().listStatements(null, RDFS.domain, (RDFNode) null)
            .toModel();
        Model shaclDomains = shaclParser.generateDomainModel();
        if (ontologyDomains.containsAll(shaclDomains)) {
            return true;
        } else {
            throw new ShaclModelTargetClassesAreNotInDomainOfRespectivePropertiesException();
        }
    }
}
