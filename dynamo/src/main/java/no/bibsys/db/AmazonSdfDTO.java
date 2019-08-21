package no.bibsys.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
import com.google.gson.internal.LinkedTreeMap;

import no.bibsys.db.structures.Entity;
import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;

public class AmazonSdfDTO {

    private static final Logger logger = LoggerFactory.getLogger(AmazonSdfDTO.class);
    public static final String CLOUDSEARCH_PRESENTAION_FIELD = "presentation_json";
    public static final String CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD = "modified";
    private static final String CLOUDSEARCH_MAPPER_QUERY_SPARQL = "cloudsearch_mapper_query.sparql";
    private static final String SEPARATOR = "§§§§";

    public enum CloudsearchOperation {
        ADD, DELETE
    }
    
    public enum EventName {
        INSERT(CloudsearchOperation.ADD), 
        MODIFY(CloudsearchOperation.ADD), 
        REMOVE(CloudsearchOperation.DELETE);
        
        public final CloudsearchOperation cloudsearchOperation;
        
        EventName(CloudsearchOperation cloudsearchSdfType) {
            this.cloudsearchOperation = cloudsearchSdfType;
        }
    }

    private final String type;
    private transient String id;
    private final transient LinkedTreeMap<String, Object> fields = new LinkedTreeMap<>();

    public AmazonSdfDTO(String eventName) {
        type =  EventName.valueOf(eventName).cloudsearchOperation.name().toLowerCase(Locale.getDefault());
    }

    public AmazonSdfDTO(CloudsearchOperation cloudsearchOperation) {
        this.type = cloudsearchOperation.name().toLowerCase(Locale.getDefault());
    }

    public AmazonSdfDTO(CloudsearchOperation cloudsearchOperation, String entityIdentifier) {
        this.type = cloudsearchOperation.name().toLowerCase(Locale.getDefault());
        this.id = entityIdentifier;
    }

    @SuppressWarnings("PMD")
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(60);
        str.append("AmazonSdfDTO [type=").append(type).append(", id=").append(id).append(", fields={");
        fields.forEach((key, value) -> str.append(key).append("=").append(value).append(", "));
        str.append("}]");
        return str.toString();
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
        RDFDataMgr.read(model, new ByteArrayInputStream(entity.getBody().toString().getBytes(StandardCharsets.UTF_8)),
                Lang.JSONLD);
        String query = IoUtils.resourceAsString(Paths.get(CLOUDSEARCH_MAPPER_QUERY_SPARQL));
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet resultSet = queryExecution.execSelect();
            List<String> resultVars = resultSet.getResultVars();
            resultSet.forEachRemaining(
                result -> resultVars.forEach(resultVar -> processQuerySolution(result, resultVar)));
        } catch (Exception e) {
            logger.debug("", e);
        }
        String presentationString = createPresentation(entity);
        fields.put(CLOUDSEARCH_PRESENTAION_FIELD, new String[] { presentationString });
        fields.put(CLOUDSEARCH_MODIFIED_TIMESTAMP_FIELD,  entity.getModified());
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
