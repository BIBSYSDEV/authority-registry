package no.bibsys.entitydata.validation.exceptions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Resource;

public class ShaclModelPropertiesAreNotIcludedInOntologyException extends ShaclModelValidationException {


    public ShaclModelPropertiesAreNotIcludedInOntologyException(String message) {
        super(message);
    }

    public static ShaclModelPropertiesAreNotIcludedInOntologyException newException(Set<Resource> resources) {
        return new ShaclModelPropertiesAreNotIcludedInOntologyException(message(resources));
    }

    private static String message(Set<Resource> resources) {
        List<String> resourceStrings = resources.stream().map(Resource::toString).collect(Collectors.toList());
        String resourcesString = String.join(",", resourceStrings);
        return "Invalid resources: " + resourcesString;
    }

}
