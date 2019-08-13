package no.bibsys.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jsonldjava.core.JsonLdOptions;
import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.writer.JsonLDWriter;
import org.apache.jena.sparql.util.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelParser {


    @JsonIgnore
    public Model parseModel(InputStream stream, Lang lang) {
        try {
            Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, stream, lang);
            return model;
        } catch (RiotException e) {
            throw new ValidationSchemaSyntaxErrorException(e);
        }
    }

    public Model parseModel(String dataString, Lang lang) {
        InputStream stream = new ByteArrayInputStream(dataString.getBytes(StandardCharsets.UTF_8));
        return parseModel(stream, lang);
    }

    @JsonIgnore
    protected Set<Resource> getUriResourceObjects(Model model) {
        return model.listObjects().toSet().stream().filter(RDFNode::isURIResource).map(rdfNode -> (Resource) rdfNode)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public String writeData(Model model, Lang lang, String frame) {
        if (lang.equals(Lang.JSONLD)) {
            return writeFormattedJsonLd(model, frame);
        } else {
            return write(model, lang);
        }
    }

    private String write(Model model, Lang lang) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, lang);
        return writer.toString();
    }

    @JsonIgnore
    public String writeFormattedJsonLd(Model model, String frame) {

        JsonLdOptions jsonLdOptions = new JsonLdOptions();
        jsonLdOptions.setOmitDefault(true);

        Context jsonLdContext = new Context();
        jsonLdContext.set(JsonLDWriter.JSONLD_CONTEXT, frame);
        // There is a suspicion that root node selection is necessary, however
        // how to generate the frame code is not clear at this time, nor
        // is it necessary for the current MVP
        // jsonLdContext.set(JsonLDWriter.JSONLD_FRAME, "{  \"type\": \"Concept\"\n }");
        jsonLdContext.set(JsonLDWriter.JSONLD_OPTIONS, jsonLdOptions);

        RDFWriter writer = RDFWriter.create().lang(Lang.JSONLD).source(model).context(jsonLdContext).build();
        return writer.asString();
    }

}
