package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import no.bibsys.utils.JsonUtils;

@Provider
@Produces({CustomMediaType.APPLICATION_RDF, CustomMediaType.APPLICATION_TURTLE, CustomMediaType.APPLICATION_RDF_XML,
    CustomMediaType.APPLICATION_N_TRIPLES, CustomMediaType.APPLICATION_JSON_LD})
public class RegistryRdfMessageBodyWriter extends CustomMessageBodyWriter<RegistryDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == RegistryDto.class;
    }

    @Override
    public void writeTo(RegistryDto registry, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream outputStream)
        throws IOException, WebApplicationException {



        Map<String, Object> metadata = registry.getMetadata();
        String body = JsonUtils.newJsonParser().writeValueAsString(metadata);

        String serialized = serializeRdf(mediaType, body);
        writerStringToOutputStream(outputStream, serialized);

    }



}
