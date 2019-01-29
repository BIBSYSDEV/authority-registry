
package no.bibsys.web.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

@Provider
@Produces({
    CustomMediaType.APPLICATION_RDF, 
    CustomMediaType.APPLICATION_TURTLE, 
    CustomMediaType.APPLICATION_RDF_XML, 
    CustomMediaType.APPLICATION_N_TRIPLES, 
    CustomMediaType.APPLICATION_JSON_LD
    })
public class EntityRdfMessageBodyWriter implements MessageBodyWriter<EntityDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == EntityDto.class;
    }

    @Override
    public void writeTo(EntityDto entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException {

        Model model = ModelFactory.createDefaultModel();

        String body = entity.getBody();
        InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, Lang.JSONLD);
        
        Lang outputLang;
        
        switch (mediaType.toString()) {
            case CustomMediaType.APPLICATION_RDF: // RDF
                outputLang = Lang.RDFJSON;
                break;
            case CustomMediaType.APPLICATION_TURTLE: // Turtle
                outputLang = Lang.TURTLE;
                break;
            case CustomMediaType.APPLICATION_RDF_XML: // XML
                outputLang = Lang.RDFXML;
                break;
            case CustomMediaType.APPLICATION_N_TRIPLES: // N-triples
                outputLang = Lang.NTRIPLES;
                break;
            case MediaType.APPLICATION_JSON: // Json
            case CustomMediaType.APPLICATION_JSON_LD:
            default:
                outputLang = Lang.JSONLD;
                break;
        }
        
        RDFDataMgr.write(entityStream, model, outputLang);
    }

}
