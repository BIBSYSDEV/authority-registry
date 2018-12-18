package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

@Provider
@Produces(MediaType.TEXT_HTML)
public class EntityMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    private static final String ENTITYTEMPLATE = "entitytemplate";

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
        
        Map<String, List<String>> bodyMap = new ConcurrentHashMap<>();
        JsonNode tree = objectMapper.readTree(entity.getBody());
        tree.fields().forEachRemaining(field -> {
            List<String> valueList = createValueList(field);
            bodyMap.put(field.getKey(), valueList);
        });
        
        entityMap.put("body",  bodyMap);
        entityMap.put("entity", entity);
        
        try(Writer writer = new PrintWriter(entityStream)){
        
            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(ENTITYTEMPLATE);
            writer.write(template.apply(entityMap));
            
            writer.flush();
        }
    }

    private List<String> createValueList(Entry<String, JsonNode> field) {
        List<String> valueList = new CopyOnWriteArrayList<>();
        if(field.getValue().isArray()) {
            field.getValue().forEach(value -> valueList.add("" + value));
        }else if (field.getValue().isInt()){
            valueList.add(Integer.toString(field.getValue().asInt()));
        }else {
            valueList.add(field.getValue().asText());
        }
        return valueList;
    }

}
