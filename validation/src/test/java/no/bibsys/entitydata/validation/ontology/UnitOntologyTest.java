package no.bibsys.entitydata.validation.ontology;

import no.bibsys.utils.IoUtils;
import no.bibsys.utils.ModelParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class UnitOntologyTest {

    private static final String ONTOLOGY_RESOURCE = "unit-entity-ontology.ttl";
    private static final String VALIDATION_FOLDER = "validation";
    private final Model ontolgoyModel;

    public UnitOntologyTest() throws IOException {
        String ontologyString = IoUtils.resourceAsString(Paths.get(VALIDATION_FOLDER, ONTOLOGY_RESOURCE));
        this.ontolgoyModel = new ModelParser().parseModel(ontologyString, Lang.TURTLE);
    }

    @Test public void preferredLabel_unitOntology_exists() {
        assertThat(ontolgoyModel.contains(UnitOntology.PREFERRED_LABEL, null), is(equalTo(true)));
    }

    @Test public void concept_unitOntology_exists() {
        assertThat(ontolgoyModel.contains(UnitOntology.CONCEPT, null), is(equalTo(true)));
    }

}
