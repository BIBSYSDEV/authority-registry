package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;

@Provider
@Produces(MediaType.TEXT_HTML)
public class RegistryMessageBodyWriter implements MessageBodyWriter<RegistryDto> {

    private static final String ID = "id";
    private static final String METADATA = "metadata";
    private static final String REGISTRYTEMPLATE = "registrytemplate";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(RegistryDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {

        Map<String, Object> entityMap = new ConcurrentHashMap<>();

        Gson gson = new Gson();
        
        LinkedHashMap<?,?> metadataMap = gson.fromJson(entity.getMetadata(), LinkedHashMap.class);
        entityMap.put(METADATA, metadataMap);
        entityMap.put(ID, entity.getId());

        try(Writer writer = new PrintWriter(entityStream)){

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(REGISTRYTEMPLATE);
            writer.write(template.apply(entityMap));

            writer.flush();
        }
    }
}
