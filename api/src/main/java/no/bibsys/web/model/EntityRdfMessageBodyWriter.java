package no.bibsys.web.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import no.bibsys.db.structures.Entity;

@Provider
@Produces({
    EntityRdfMessageBodyWriter.MEDIATYPE_JSON_LD, 
    EntityRdfMessageBodyWriter.MEDIATYPE_N_TRIPLES, 
    EntityRdfMessageBodyWriter.MEDIATYPE_RDF_XML, 
    EntityRdfMessageBodyWriter.MEDIATYPE_TURTLE, 
    EntityRdfMessageBodyWriter.MEDIATYPE_RDF,
    MediaType.APPLICATION_JSON})
public class EntityRdfMessageBodyWriter implements MessageBodyWriter<Entity> {

    static final String MEDIATYPE_RDF = "application/rdf";
    static final String MEDIATYPE_TURTLE = "application/turtle";
    static final String MEDIATYPE_RDF_XML = "application/rdf+xml";
    static final String MEDIATYPE_N_TRIPLES = "application/n-triples";
    static final String MEDIATYPE_JSON_LD = "application/ld+json";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Entity.class;
    }

    @Override
    public void writeTo(Entity entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        Model model = ModelFactory.createDefaultModel();

        InputStream stream = new ByteArrayInputStream(entity.getBody().toString().getBytes(StandardCharsets.UTF_8));
        RDFDataMgr.read(model, stream, Lang.TURTLE);
        
        Lang outputLang = Lang.JSONLD;
        
        switch(mediaType.toString()) {
        case MEDIATYPE_RDF:
        case MediaType.APPLICATION_JSON:
            outputLang = Lang.RDFJSON;
            break;
        case MEDIATYPE_TURTLE:
            outputLang = Lang.TURTLE;
            break;
        case MEDIATYPE_RDF_XML:
            outputLang = Lang.RDFXML;
            break;
        case MEDIATYPE_N_TRIPLES:
            outputLang = Lang.NTRIPLES;
            break;
        case MEDIATYPE_JSON_LD:
        default:
            outputLang = Lang.JSONLD;
            break;
        }
        
        RDFDataMgr.write(entityStream, model, outputLang);
    }

}
