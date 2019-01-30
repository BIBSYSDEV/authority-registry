package no.bibsys.web.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public abstract class CustomMessageBodyWriter<T> implements MessageBodyWriter<T> {


    protected String serialize(MediaType mediaType, String body) {

        Model model = ModelFactory.createDefaultModel();
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
        StringWriter writer = new StringWriter();

        RDFDataMgr.write(writer, model, outputLang);
        return writer.toString();
    }

    protected void writerStringToOutputStream(OutputStream entityStream, String serialized) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(entityStream);
        writer.write(serialized);
        writer.flush();
        writer.close();
    }

}
