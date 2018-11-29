package no.bibsys.db.exceptions;

public class RegistryUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 8268702995756002670L;

    public RegistryUnavailableException(String registryName, String reason) {
        super(String.format(
                "Registry with name %s is being %s, check status at /registry/%s/status",
                registryName, reason, registryName));
    }
}
