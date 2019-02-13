package no.bibsys.web.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CustomMediaTypeTest {

    @Test
    public void testValues() {
        assertThat(CustomMediaType.APPLICATION_JSON_LD, is("application/ld+json"));
        assertThat(CustomMediaType.APPLICATION_MARC, is("application/marc"));
        assertThat(CustomMediaType.APPLICATION_MARCXML, is("application/marcxml"));
        assertThat(CustomMediaType.APPLICATION_MARCXML_XML, is("application/marcxml+xml"));
        assertThat(CustomMediaType.APPLICATION_N_TRIPLES, is("application/n-triples"));
        assertThat(CustomMediaType.APPLICATION_RDF, is("application/rdf"));
        assertThat(CustomMediaType.APPLICATION_RDF_XML, is("application/rdf+xml"));
        assertThat(CustomMediaType.APPLICATION_TURTLE, is("application/turtle"));
    }

}
