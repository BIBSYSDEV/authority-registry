package no.bibsys.web.model;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({
    MediaTypeRdf.APPLICATION_RDF, 
    MediaTypeRdf.APPLICATION_TURTLE, 
    MediaTypeRdf.APPLICATION_RDF_XML, 
    MediaTypeRdf.APPLICATION_N_TRIPLES, 
    MediaTypeRdf.APPLICATION_JSON_LD
})
public class RegistryRdfMessageBodyWriter implements MessageBodyWriter<RegistryDto> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return type == RegistryDto.class;
    }

    @Override
    public void writeTo(RegistryDto registry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {

//        Model model = ModelFactory.createDefaultModel();
//
//        Map<String, Object> metadata = registry.getMetadata();
//
//        InputStream stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
//        RDFDataMgr.read(model, stream, Lang.JSONLD);
//        
//        Lang outputLang;
//        
//        switch (mediaType.toString()) {
//            case MediaTypeRdf.APPLICATION_RDF: // RDF
//                outputLang = Lang.RDFJSON;
//                break;
//            case MediaTypeRdf.APPLICATION_TURTLE: // Turtle
//                outputLang = Lang.TURTLE;
//                break;
//            case MediaTypeRdf.APPLICATION_RDF_XML: // XML
//                outputLang = Lang.RDFXML;
//                break;
//            case MediaTypeRdf.APPLICATION_N_TRIPLES: // N-triples
//                outputLang = Lang.NTRIPLES;
//                break;
//            case MediaType.APPLICATION_JSON: // Json
//            case MediaTypeRdf.APPLICATION_JSON_LD:
//            default:
//                outputLang = Lang.JSONLD;
//                break;
//        }
//        
//        RDFDataMgr.write(entityStream, model, outputLang);
    }
}
