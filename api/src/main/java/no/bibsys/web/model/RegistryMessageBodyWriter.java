package no.bibsys.web.model;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jsonldjava.core.JsonLdConsts;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Produces(MediaType.TEXT_HTML)
public class RegistryMessageBodyWriter implements MessageBodyWriter<RegistryDto> {

    private static final String ID = "id";
    private static final String METADATA = "metadata";
    private static final String REGISTRY_TEMPLATE = "registrytemplate";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == RegistryDto.class;
    }

    @Override
    public void writeTo(RegistryDto registry, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        Map<String, Object> registryMap = new ConcurrentHashMap<>();

        Map<?, ?> metadataMap = new ConcurrentHashMap<>(registry.getMetadata());
        metadataMap.remove(JsonLdConsts.CONTEXT);
        registryMap.put(METADATA, metadataMap);
        registryMap.put(ID, registry.getId());

        try (Writer writer = new PrintWriter(entityStream)) {

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(REGISTRY_TEMPLATE);
            writer.write(template.apply(registryMap));

            writer.flush();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
