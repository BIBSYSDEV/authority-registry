package no.bibsys.web.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import no.bibsys.utils.JsonUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

@Provider
@Produces({CustomMediaType.APPLICATION_RDF, CustomMediaType.APPLICATION_TURTLE, CustomMediaType.APPLICATION_RDF_XML,
    CustomMediaType.APPLICATION_N_TRIPLES, CustomMediaType.APPLICATION_JSON_LD})
public class RegistryRdfMessageBodyWriter implements MessageBodyWriter<RegistryDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == RegistryDto.class;
    }

    @Override
    public void writeTo(RegistryDto registry, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException {

        Model model = ModelFactory.createDefaultModel();

        Map<String, Object> metadata = registry.getMetadata();
        String body = JsonUtils.newJsonParser().writeValueAsString(metadata);

        InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, Lang.JSONLD);

        Lang outputLang;

        switch (mediaType.toString()) {
            case CustomMediaType.APPLICATION_TURTLE:
                outputLang = Lang.TURTLE;
                break;
            case CustomMediaType.APPLICATION_RDF:
            case CustomMediaType.APPLICATION_RDF_XML:
                outputLang = Lang.RDFXML;
                break;
            case CustomMediaType.APPLICATION_N_TRIPLES:
                outputLang = Lang.NTRIPLES;
                break;
            case MediaType.APPLICATION_JSON:
            case CustomMediaType.APPLICATION_JSON_LD:
            default:
                outputLang = Lang.JSONLD;
                break;
        }

        RDFDataMgr.write(entityStream, model, outputLang);
    }
}
