package no.bibsys.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import no.bibsys.db.structures.Entity;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;

public class AmazonSdfDTO {

    private static final Logger logger = LoggerFactory.getLogger(AmazonSdfDTO.class);
    public static final String CLOUDSEARCH_PRESENTAION_FIELD = "presentation_json";
    public static final String CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD = "modified";
    private static final String CLOUDSEARCH_MAPPER_QUERY_SPARQL = "cloudsearch_mapper_query.sparql";
    private static final String SEPARATOR = "§§§§";

    public enum CloudsearchSdfType {
        ADD, DELETE
    }
    
    public enum EventName {
        INSERT, MODIFY, REMOVE
    }

    private final String type;
    private transient String id;
    private final transient Map<String, String[]> fields = new ConcurrentHashMap<>();

    public AmazonSdfDTO(String eventName) {
        type = eventToOperation(eventName).name().toLowerCase(Locale.getDefault());
    }

    public AmazonSdfDTO(CloudsearchSdfType cloudsearchSdfType) {
        this.type = cloudsearchSdfType.name().toLowerCase(Locale.getDefault());
    }

    @SuppressWarnings("PMD")
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(60);
        str.append("AmazonSdfDTO [type=").append(type).append(", id=").append(id).append(", fields={");
        fields.forEach((k, v) -> str.append(k).append("=").append(Arrays.toString(fields.get(k))).append(", "));
        str.append("}]");
        return str.toString();
    }

    private CloudsearchSdfType eventToOperation(String eventName) {
        EventName event = EventName.valueOf(eventName);
        switch (event) {
            case INSERT:
            case MODIFY:
                return CloudsearchSdfType.ADD;
            case REMOVE:
                return CloudsearchSdfType.DELETE;
            default:
                return null;
        }
    }

    public Map<String, ?> getFields() {
        return fields;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFieldsFromEntity(Entity entity) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, new ByteArrayInputStream(entity.getBody().toString().getBytes(Charsets.UTF_8)),
                Lang.JSONLD);
        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, model, Lang.TURTLE);
        String query = IoUtils.resourceAsString(Paths.get(CLOUDSEARCH_MAPPER_QUERY_SPARQL));
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet resultSet = queryExecution.execSelect();
            List<String> resultVars = resultSet.getResultVars();
            resultSet.forEachRemaining(
                result -> resultVars.stream().forEach(resultVar -> processQuerySolution(result, resultVar)));
        } catch (Exception e) {
            logger.debug("", e);
        }
        String presentationString = createPresentation(entity);
        fields.put(CLOUDSEARCH_PRESENTAION_FIELD, new String[] { presentationString });
        fields.put(CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD, new String[] { entity.getModified() });
    }

    private String createPresentation(Entity entity) throws JsonGenerationException, JsonMappingException, IOException {
        StringWriter presentationWriter = new StringWriter();
        ObjectMapper objectMapper = JsonUtils.newJsonParser();
        objectMapper.writeValue(presentationWriter, entity);
        return presentationWriter.toString();
    }

    private void processQuerySolution(QuerySolution querySolution, String resultVar) {
        Optional.ofNullable(querySolution.get(resultVar)).ifPresent(value -> {
            fields.put(resultVar, value.asLiteral().getString().split(SEPARATOR));
        });
    }
}
