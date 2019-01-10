package no.bibsys.entitydata.validation.rdfutils;

import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class RdfUtil {

    public static String turtleString(Model model) {
        StringWriter writer = new StringWriter();
        RDFDataMgr.write(writer, model, Lang.TURTLE);
        return writer.toString();
    }
}
