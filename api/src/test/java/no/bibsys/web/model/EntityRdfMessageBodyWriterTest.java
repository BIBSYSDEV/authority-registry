package no.bibsys.web.model;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EntityRdfMessageBodyWriterTest {

    private static final String FRAMED_JSONLD = "/model/full_humord_style_framed_output.jsonld";
    private static final String TEST_ENTITY_JSONLD = "/model/full_humord_style_entity.jsonld";
    private static final String SOME_DATE = "2019-08-09";
    private static final String SOME_ID = "someId";

    @Test
    public void isWriteable_isActuallyWriteable() {
        EntityRdfMessageBodyWriter entityRdfMessageBodyWriter = new EntityRdfMessageBodyWriter();
        EntityDto entityDto = new EntityDto();
        Annotation[] annotations = new Annotation[0];
        assertTrue(entityRdfMessageBodyWriter.isWriteable(entityDto.getClass(),
                String.class, annotations, MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void isWriteable_isNotWriteable() {
        EntityRdfMessageBodyWriter entityRdfMessageBodyWriter = new EntityRdfMessageBodyWriter();
        Annotation[] annotations = new Annotation[0];
        assertFalse(entityRdfMessageBodyWriter.isWriteable(String.class,
                String.class, annotations, MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void writeTo_writesValidJsonLd() throws IOException {
        EntityDto entityDto = new EntityDto();
        entityDto.setId(SOME_ID);
        entityDto.setCreated(SOME_DATE);
        entityDto.setModified(SOME_DATE);
        entityDto.setBody(
                IOUtils.resourceToString(TEST_ENTITY_JSONLD, UTF_8));
        EntityRdfMessageBodyWriter entityRdfMessageBodyWriter = new EntityRdfMessageBodyWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        entityRdfMessageBodyWriter.writeTo(entityDto, String.class, String.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE, new MultivaluedHashMap<>(), byteArrayOutputStream);
        String output = byteArrayOutputStream.toString();
        assertThat(output, is(equalTo(IOUtils.resourceToString(FRAMED_JSONLD, UTF_8))));
    }
}