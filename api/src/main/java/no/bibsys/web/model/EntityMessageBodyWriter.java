package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

@Provider
@Produces("text/html")
public class EntityMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        
        Map<String, Object> entityMap = new ConcurrentHashMap<>();
        
        ObjectMapper objectMapper = new ObjectMapper();
        entityMap.put("body",  (Map<String, String[]>)objectMapper.readValue(entity.getBody(), Map.class));
        entityMap.put("entity", entity);
        
        try(Writer writer = new PrintWriter(entityStream)){
        
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile("entitytemplate");
            writer.write(template.apply(entityMap));
            
            writer.flush();
        }
    }

}
