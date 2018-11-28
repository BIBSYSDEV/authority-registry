package no.bibsys.web.exception;

public class RegistryAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -6440401193164668011L;

    public RegistryAlreadyExistsException(String registryName) {
        super(String.format("A registry with name %s already exists", registryName));

    }

}
