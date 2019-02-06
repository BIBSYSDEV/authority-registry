package no.bibsys.web.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.aws.tools.JsonUtils;

@Provider
@Produces({CustomMediaType.APPLICATION_MARC, CustomMediaType.APPLICATION_MARCXML, CustomMediaType.APPLICATION_MARCXML_XML})
public class EntityMarcMessageBodyWriter extends CustomMessageBodyWriter<EntityDto> {

    private static final String PREFERRED_LABEL_FIELD = "100";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        String body = entity.getBody();
        String preferredLabelValue = extractPreferredLabel(body);
        
        MarcFactory factory = MarcFactory.newInstance();
        Record record = factory.newRecord();
        record.addVariableField(factory.newControlField("001", "111"));
        DataField preferredLabelField = factory.newDataField();
        preferredLabelField.setTag(PREFERRED_LABEL_FIELD);
        preferredLabelField.addSubfield(factory.newSubfield('a', preferredLabelValue));
        record.addVariableField(preferredLabelField);
        
        OutputStream baos = new ByteArrayOutputStream();;
        MarcWriter writer = new MarcXmlWriter(baos, true);
        writer.write(record);
        writer.close();
        
        System.out.println(baos.toString());
    }

    private String extractPreferredLabel(String body) throws IOException, JsonParseException, JsonMappingException {
        ObjectMapper objectMapper = JsonUtils.newJsonParser();
        Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);
        
        List<Map<String, String>> preferredLabelList = (List<Map<String, String>>) bodyMap.get("preferredLabel");
        String preferredLabelValue = "";
        for (Map<String, String> map : preferredLabelList) {
            if("no".equals(map.get("@language")) || ("".equals(preferredLabelValue) && "en".equals(map.get("@language")))) {
                preferredLabelValue = map.get("@value");
            }
        }
        return preferredLabelValue;
    }

}
