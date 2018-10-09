package no.bibsys.handlers;

public class CreateRegistryRequest extends EditRegistryRequest {

    public CreateRegistryRequest() {
        super(EditRegistryRequest.CREATE);
    }


    public CreateRegistryRequest(String registryName) {
        super(EditRegistryRequest.CREATE, registryName);
    }


}
