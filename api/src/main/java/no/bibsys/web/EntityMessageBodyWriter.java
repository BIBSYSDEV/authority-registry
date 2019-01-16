package no.bibsys.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import no.bibsys.web.model.EntityDto;

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
        
        try(Writer writer = new PrintWriter(entityStream)){
        
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile("entitytemplate");
            writer.write(template.apply(entity));
            
            writer.flush();
        }
    }

}
