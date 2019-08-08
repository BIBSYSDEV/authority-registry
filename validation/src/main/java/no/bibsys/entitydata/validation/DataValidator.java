package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.utils.ModelParser;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

public class DataValidator extends ModelParser {

    private static final Property SH_CONFORMS =
            ResourceFactory.createProperty("http://www.w3.org/ns/shacl#conforms");
    private static final Literal BOOLEAN_FALSE =
            ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean);
    private static final String SHACL = "http://www.w3.org/ns/shacl#";
    private static final String SHACL_PREFIX = "sh";
    private static final String EIGHT_SPACES = "        ";
    private final transient Model validationSchema;
    private transient Model report;

    public DataValidator(Model validationSchema) {
        super();
        this.validationSchema = validationSchema;
    }

    public Model validationReport(Model dataModel) {
        return shaclValidation(dataModel);
    }

    public boolean isValidEntry(Model dataModel) throws EntityFailedShaclValidationException {
        if (validationResult(dataModel)) {
            return true;
        } else {
            throw new EntityFailedShaclValidationException(formatValidationReport(report));
        }
    }

    public boolean validationResult(Model dataModel) {
        if (checkModelIfEmpty(dataModel)) {
            return false;
        } else {
            report = shaclValidation(dataModel);

            return parseReportToBoolean(report);
        }
    }

    private String formatValidationReport(Model validationReport) {
        StringWriter stringWriter = new StringWriter();
        Map<String, String> prefixMapping = validationReport.getNsPrefixMap();

        if (!prefixMapping.containsKey(SHACL_PREFIX)) {
            validationReport.setNsPrefix(SHACL_PREFIX, SHACL);
        }

        RDFDataMgr.write(stringWriter, validationReport, Lang.TURTLE);
        return formatOutputForConsole(stringWriter.toString());
    }

    /**
     * Hardcoded check of empty models because an empty model is valid model according to Schacl validation schemas.
     *
     * @param dataModel A data model
     * @return true if model is empty, false otherwise
     */
    private boolean checkModelIfEmpty(Model dataModel) {
        return dataModel.isEmpty();
    }

    private boolean parseReportToBoolean(Model report) {
        return !report.contains(null, SH_CONFORMS, BOOLEAN_FALSE);
    }

    private Model shaclValidation(Model dataModel) {
        return ValidationUtil.validateModel(dataModel, validationSchema, true).getModel();
    }

    private String formatOutputForConsole(String string) {
        Pattern pattern = Pattern.compile("^", Pattern.MULTILINE);
        return pattern.matcher(string).replaceAll(EIGHT_SPACES);
    }
}
