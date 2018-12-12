package no.bibsys.entitydata.validation;

import no.bibsys.entitydata.validation.rdfutils.RdfUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.topbraid.shacl.validation.ValidationUtil;

public class ShaclValidator implements ModelParser {
    private static final Property SH_CONFORMS = ResourceFactory.createProperty("http://www.w3.org/ns/shacl#conforms");
    private static final Literal BOOLEAN_FALSE = ResourceFactory
        .createTypedLiteral("false", XSDDatatype.XSDboolean);


    private final Model validationSchema;

    public ShaclValidator(Model validationSchema) {
        this.validationSchema = validationSchema;
    }

    public Model validationReport(Model dataModel){
       return  validate(dataModel);
    }


    public boolean validationResult(Model dataModel) {
        Model report = validate(dataModel);
        System.out.println(RdfUtil.turtleString(report));
        return parseReportToBoolean(report);
    }


    private boolean parseReportToBoolean(Model report) {
        return !report.contains(null, SH_CONFORMS, BOOLEAN_FALSE);

    }


    private Model validate(Model dataModel) {
        return ValidationUtil
            .validateModel(dataModel, validationSchema, true)
            .getModel();
    }


    public Model getValidationSchema() {
        return validationSchema;
    }


}
