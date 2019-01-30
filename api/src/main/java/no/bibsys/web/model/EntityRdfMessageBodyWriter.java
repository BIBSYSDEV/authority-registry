package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({CustomMediaType.APPLICATION_RDF, CustomMediaType.APPLICATION_TURTLE, CustomMediaType.APPLICATION_RDF_XML,
    CustomMediaType.APPLICATION_N_TRIPLES, CustomMediaType.APPLICATION_JSON_LD})
public class EntityRdfMessageBodyWriter extends CustomMessageBodyWriter<EntityDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        String body = entity.getBody();
        String serialized = serialize(mediaType, body);
        writerStringToOutputStream(entityStream, serialized);
    }

}
