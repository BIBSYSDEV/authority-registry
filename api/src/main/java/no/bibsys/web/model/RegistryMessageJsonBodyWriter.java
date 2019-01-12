package no.bibsys.web.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import no.bibsys.utils.JsonUtils;


@Provider
@Produces(MediaType.APPLICATION_JSON)
public class RegistryMessageJsonBodyWriter implements MessageBodyWriter<RegistryDto> {

    public static final String METADATA_FIELD = "metadata";
    private static final String ID = "id";
    private static final String METADATA = "metadata";
    private static final String REGISTRY_TEMPLATE = "registrytemplate";
    private final transient ObjectMapper mapper = JsonUtils.newJsonParser();


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {

        return type == RegistryDto.class;
    }

    @Override
    public void writeTo(RegistryDto registry, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException {

        ObjectNode registryDtoNode = mapper.convertValue(registry, ObjectNode.class);
        ObjectNode metadataNode = mapToObjectNode(registry.getMetadata());
        registryDtoNode.replace(METADATA_FIELD, metadataNode);

        try (Writer writer = new PrintWriter(entityStream)) {

            mapper.writeValue(writer, registryDtoNode);
            writer.flush();
        }
    }


    private ObjectNode mapToObjectNode(Map<String, Object> map) {
        ObjectNode root = mapper.createObjectNode();

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = map.get(key);
            if (value instanceof String) {
                root.put(key, (String) value);
            } else {
                Map<String, Object> valueMap = (Map<String, Object>) map.get(key);
                ObjectNode objectNode = mapToObjectNode(valueMap);
                root.set(key, objectNode);
            }
        }

        return root;

    }
}
