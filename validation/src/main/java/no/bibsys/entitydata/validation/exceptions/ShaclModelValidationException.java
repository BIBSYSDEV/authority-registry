package no.bibsys.entitydata.validation.exceptions;

public class ShaclModelValidationException extends Exception {

    private static final long serialVersionUID = -5540077865120287157L;


    public ShaclModelValidationException(){
        super();
    }


    public ShaclModelValidationException(String message){
        super(message);
    }

}
