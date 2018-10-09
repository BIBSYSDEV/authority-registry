package no.bibsys.handlers;

public class EmptyRegistryRequest extends EditRegistryRequest {


    public EmptyRegistryRequest() {
        super(EditRegistryRequest.EMPTY);
    }

    public EmptyRegistryRequest(String registryName) {
        super(EditRegistryRequest.EMPTY, registryName);
    }
}
