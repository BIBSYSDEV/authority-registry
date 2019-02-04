package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jsonldjava.core.JsonLdConsts;

import no.bibsys.aws.tools.JsonUtils;
import no.bibsys.entitydata.validation.ontology.UnitOntology;
import no.bibsys.utils.ModelParser;

@Provider
@Produces(MediaType.TEXT_HTML)
public class EntityHtmlMessageBodyWriter extends ModelParser implements MessageBodyWriter<EntityDto> {

    private static final String NO_LABEL = "(No label)";

    private static final String LANG_NO = "no";
    private static final String LANG_EN = "en";
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String ENTITY_TEMPLATE = "entitytemplate";
    private static final String LABEL = "label";
    public static final Lang SUPPORTED_LANGUAGE = Lang.JSONLD;


    @Override
    @Produces({MediaType.TEXT_HTML})
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        Map<String, Object> entityMap = createEntityMap(entity);

        try (Writer writer = new PrintWriter(entityStream)) {

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compile(ENTITY_TEMPLATE);
            writer.write(template.apply(entityMap));

            writer.flush();
        }
    }

    private Map<String, Object> createEntityMap(EntityDto entity) throws IOException {
        Map<String, Object> entityMap = new ConcurrentHashMap<>();

        Map<?, ?> bodyMap = JsonUtils.newJsonParser().readValue(entity.getBody(), Map.class);
        Model model = parseModel(entity.getBody(), SUPPORTED_LANGUAGE);

        bodyMap.remove(JsonLdConsts.CONTEXT);
        entityMap.put(BODY, bodyMap);
        entityMap.put(ID, extractId(model));
        entityMap.put(LABEL, extractTitleFromPreferredLabel(model));

        return entityMap;
    }

    private String extractId(Model model) {
        Resource resource = model.listSubjectsWithProperty(RDF.type, UnitOntology.CONCEPT).nextResource();
        return resource.toString();
    }

    private String extractTitleFromPreferredLabel(Model model) {
        String label = NO_LABEL;
        Map<String, String> labels = model.listObjectsOfProperty(UnitOntology.PREFERRED_LABEL)
            .filterKeep(rdfNode -> rdfNode.isLiteral()).mapWith(rdfNode -> rdfNode.asLiteral()).toList().stream()
            .collect(Collectors.toMap(Literal::getLanguage, lit -> lit.getValue().toString()));
        if (labels.containsKey(LANG_NO)) {
            label = labels.get(LANG_NO);
        } else if (labels.containsKey(LANG_EN)) {
            label = labels.get(LANG_EN);
        } else if (!label.isEmpty()) {
            label = labels.values().iterator().next();
        }
        return label;

    }
}
