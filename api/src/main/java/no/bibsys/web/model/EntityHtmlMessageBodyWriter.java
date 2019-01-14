package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;

@Provider
@Produces(MediaType.TEXT_HTML)
public class EntityHtmlMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    private static final String NO_LABEL = "(No label)";
    private static final String VALUE = "value";
    private static final String LANG = "lang";
    private static final String LANG_NO = "no";
    private static final String LANG_EN = "en";
    private static final String PREFERRED_LABEL = "preferredLabel";
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String ENTITY_TEMPLATE = "entitytemplate";
    private static final String LABEL = "label";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException {

        Map<String, Object> entityMap = createEntityMap(entity);

        try(Writer writer = new PrintWriter(entityStream)){

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(ENTITY_TEMPLATE);
            writer.write(template.apply(entityMap));

            writer.flush();
        }
    }

    private Map<String, Object> createEntityMap(EntityDto entity) {
        Map<String, Object> entityMap = new ConcurrentHashMap<>();

        Gson gson = new Gson();
        
        Map<?,?> bodyMap = gson.fromJson(entity.getBody(), Map.class);
        entityMap.put(BODY, bodyMap);
        entityMap.put(ID, entity.getId());
        List<?> preferredLabel = (List<?>)bodyMap.get(PREFERRED_LABEL);
        String label = findTitle(preferredLabel);
        entityMap.put(LABEL, label);
        return entityMap;
    }

    private String findTitle(List<?> preferredLabel) {
        String label = NO_LABEL;
        if(Objects.nonNull(preferredLabel)) {
            @SuppressWarnings("unchecked")
            Map<String, String> titleMap = preferredLabel.stream().filter(labelObject -> 
            ((Map<String, String>)labelObject).get(LANG).equals(LANG_EN)||
            ((Map<String, String>)labelObject).get(LANG).equals(LANG_NO))
            .collect(Collectors.toMap(
                    labelObject -> ((Map<String, String>)labelObject).get(LANG), 
                    labelObject -> ((Map<String,String>)labelObject).get(VALUE)));
            if(Objects.nonNull(titleMap.get(LANG_NO))) {
                label = titleMap.get(LANG_NO);
            } else if(Objects.nonNull(titleMap.get(LANG_EN))) {
                label = titleMap.get(LANG_EN);
            }
        }
        return label;
    }
}
