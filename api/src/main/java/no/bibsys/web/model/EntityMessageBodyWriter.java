package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
public class EntityMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String ENTITYTEMPLATE = "entitytemplate";
    private static final String LABEL = "label";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {

        Map<String, Object> entityMap = new ConcurrentHashMap<>();

        Gson gson = new Gson();
        
        LinkedHashMap<?,?> bodyMap = gson.fromJson(entity.getBody(), LinkedHashMap.class);
        entityMap.put(BODY, bodyMap);
        entityMap.put(ID, entity.getId());
        List<?> prefferedLabel = (List<?>)bodyMap.get("prefferedLabel");
        String label = findTitle(prefferedLabel);
        entityMap.put(LABEL, label);

        try(Writer writer = new PrintWriter(entityStream)){

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(ENTITYTEMPLATE);
            writer.write(template.apply(entityMap));

            writer.flush();
        }
    }

    private String findTitle(List<?> prefferedLabel) {
        String label = "(No label)";
        if(!Objects.isNull(prefferedLabel)) {
            Map<String, String> titleMap = prefferedLabel.stream().filter(labelObject -> 
            ((Map<String, String>)labelObject).get("lang").equals("en")||
            ((Map<String, String>)labelObject).get("lang").equals("no"))
            .collect(Collectors.toMap(
                    labelObject -> ((Map<String, String>)labelObject).get("lang"), 
                    labelObject -> ((Map<String,String>)labelObject).get("value")));
            if(!Objects.isNull(titleMap.get("no"))) {
                label = titleMap.get("no");
            } else if(!Objects.isNull(titleMap.get("en"))) {
                label = titleMap.get("en");
            }
        }
        return label;
    }
}
