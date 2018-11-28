package no.bibsys.web.exception;

public class RegistryNotEmptyException  extends RuntimeException {

    private static final long serialVersionUID = 2795729621873587834L;

    public RegistryNotEmptyException(String registryName) {
        super(String.format("Registry with name %s is not empty", registryName));

    }

}
