package no.bibsys.web.model;

import com.github.jsonldjava.core.JsonLdOptions;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.writer.JsonLDWriter;
import org.apache.jena.sparql.util.Context;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public abstract class CustomMessageBodyWriter<T> implements MessageBodyWriter<T> {


    private static final String JSON_LD_FRAME = "/writer/entitydata_frame.jsonld";
    private static final String FILE_NOT_FOUND_TEMPLATE = "Could not find file %s";

    protected String serializeRdf(MediaType mediaType, String body) {

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

        if (outputLang == Lang.JSONLD) {
            return writeFormattedJsonLd(model);
        }

        StringWriter writer = new StringWriter();

        RDFDataMgr.write(writer, model, outputLang);
        return writer.toString();
    }

    private String writeFormattedJsonLd(Model model) {
        String contextObject;
        try {
            contextObject = IOUtils.resourceToString(JSON_LD_FRAME, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(String.format(FILE_NOT_FOUND_TEMPLATE, JSON_LD_FRAME));
        }

        JsonLdOptions jsonLdOptions = new JsonLdOptions();
        jsonLdOptions.setOmitDefault(true);

        Context jsonLdContext = new Context();
        jsonLdContext.set(JsonLDWriter.JSONLD_CONTEXT, contextObject);
        // There is a suspicion that root node selection is necessary, however
        // how to generate the frame code is not clear at this time, nor
        // is it necessary for the current MVP
        // jsonLdContext.set(JsonLDWriter.JSONLD_FRAME, "{  \"type\": \"Concept\"\n }");
        jsonLdContext.set(JsonLDWriter.JSONLD_OPTIONS, jsonLdOptions);

        RDFWriter writer = RDFWriter.create().lang(Lang.JSONLD).source(model).context(jsonLdContext).build();
        return writer.asString();
    }

    protected void writerStringToOutputStream(OutputStream entityStream, String serialized) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream);
        writer.write(serialized);
        writer.flush();
        writer.close();
    }

}
