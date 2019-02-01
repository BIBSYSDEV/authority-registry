
package no.bibsys.web.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({
    CustomMediaType.APPLICATION_MADS_XML, 
    CustomMediaType.APPLICATION_MARC, 
    CustomMediaType.APPLICATION_MARCXML, 
    CustomMediaType.APPLICATION_MARCXML_XML 
    })
public class EntityMarcMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException {

        String body = entity.getBody();
        InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        
        
        switch (mediaType.toString()) {
            case CustomMediaType.APPLICATION_MADS_XML:
                break;
            case CustomMediaType.APPLICATION_MARC:
                break;
            case CustomMediaType.APPLICATION_MARCXML:
                break;
            case CustomMediaType.APPLICATION_MARCXML_XML:
                break;
            default:
                break;
        }
    }
}
