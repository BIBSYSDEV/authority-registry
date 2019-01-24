package no.bibsys.db.exceptions;

public class RegistryUnavailableException extends RuntimeException {


    public RegistryUnavailableException(String registryName, String reason) {
        super(String.format(
                "Registry with name %s is being %s, check status at /registry/%s/status",
                registryName, reason, registryName));
    }
}
