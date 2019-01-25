package no.bibsys.web.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
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
import no.bibsys.aws.tools.JsonUtils;
import no.bibsys.entitydata.validation.ModelParser;
import no.bibsys.entitydata.validation.ontology.UnitOntology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;

@Provider
@Produces(MediaType.TEXT_HTML)
public class EntityHtmlMessageBodyWriter extends ModelParser implements
    MessageBodyWriter<EntityDto> {

    private static final String NO_LABEL = "(No label)";

    private static final String LANG_NO = "no";
    private static final String LANG_EN = "en";
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String ENTITY_TEMPLATE = "entitytemplate";
    private static final String LABEL = "label";

    private final transient ObjectMapper jsonParser = JsonUtils.newJsonParser();

    @Override
    @Produces({MediaType.TEXT_HTML})
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException {

        Map<String, Object> entityMap = new ConcurrentHashMap<>();

        Model body = parseModel(entity.getBody(), Lang.JSONLD);
        Map<String, String> preferredLabel = extractPreferredLabel(body);

        LinkedHashMap<?, ?> bodyMap = jsonParser.readValue(entity.getBody(), LinkedHashMap.class);
        entityMap.put(BODY, bodyMap);
        entityMap.put(ID, entity.getId());

        String label = findTitle(preferredLabel);
        entityMap.put(LABEL, label);

        try (Writer writer = new PrintWriter(entityStream)) {

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(ENTITY_TEMPLATE);
            writer.write(template.apply(entityMap));

            writer.flush();
        }
    }

    private Map<String, String> extractPreferredLabel(Model body) {
        return body
                .listObjectsOfProperty(UnitOntology.PREFERRED_LABEL).mapWith(RDFNode::asLiteral)
                .toList().stream()
                .collect(Collectors.toMap(lit -> lit.getLanguage(), lit -> lit.getValue().toString()));
    }

    private String findTitle(Map<String, String> preferredLabel) {
        String label = NO_LABEL;
        if (Objects.nonNull(preferredLabel)) {
            if (preferredLabel.containsKey(LANG_NO)) {
                label = preferredLabel.get(LANG_NO);
            } else if (preferredLabel.containsKey(LANG_EN)) {
                label = preferredLabel.get(LANG_EN);
            }

        }

        return label;
    }
}
