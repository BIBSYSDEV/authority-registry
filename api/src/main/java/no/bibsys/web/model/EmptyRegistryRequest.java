package no.bibsys.web.model;

public class EmptyRegistryRequest extends EditRegistryRequest {


    public EmptyRegistryRequest() {
        super(EditRegistryRequest.EMPTY);
    }

    public EmptyRegistryRequest(String registryName) {
        super(EditRegistryRequest.EMPTY, registryName);
    }
}
