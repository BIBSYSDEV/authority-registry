package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.exceptions.EntryFailedShaclValidationException;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class DataValidator extends ModelParser {

    private static final Property SH_CONFORMS = ResourceFactory
        .createProperty("http://www.w3.org/ns/shacl#conforms");
    private static final Literal BOOLEAN_FALSE = ResourceFactory
        .createTypedLiteral("false", XSDDatatype.XSDboolean);
    private final transient Model validationSchema;

    public DataValidator(Model validationSchema) {
        super();
        this.validationSchema = validationSchema;
    }

    public Model validationReport(Model dataModel) {
        return shaclValidation(dataModel);
    }


    public boolean isValidEntry(Model dataModel) throws EntryFailedShaclValidationException {
        if(!validationResult(dataModel)){
            throw new EntryFailedShaclValidationException() ;
        }
        else{
            return true;
        }

    }

    public boolean validationResult(Model dataModel) {
        Model report = shaclValidation(dataModel);
        return parseReportToBoolean(report);
    }

    private boolean parseReportToBoolean(Model report) {
        return !report.contains(null, SH_CONFORMS, BOOLEAN_FALSE);
    }

    private Model shaclValidation(Model dataModel) {
        return ValidationUtil
            .validateModel(dataModel, validationSchema, true)
            .getModel();
    }

    public Model getValidationSchema() {
        return validationSchema;
    }

}
