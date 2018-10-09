package no.bibsys.handlers;

public class EditRegistryRequest {

    public static final String CREATE = "create";
    public static final String EMPTY = "empty";


    private String action;
    private String registryName;


    public EditRegistryRequest() {
    }


    public EditRegistryRequest(String action) {
        this.action = action;
    }


    public EditRegistryRequest(String action, String registryName) {
        this(action);
        this.registryName = registryName;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EditRegistryRequest)) {
            return false;
        }

        EditRegistryRequest that = (EditRegistryRequest) o;

        if (action != null ? !action.equals(that.action) : that.action != null) {
            return false;
        }
        return registryName != null ? registryName.equals(that.registryName)
            : that.registryName == null;
    }

    @Override
    public final int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (registryName != null ? registryName.hashCode() : 0);
        return result;
    }


    public final String getAction() {
        return action;
    }


    //TODO should PMD rule "Bean member should serialize" be disabled or not?
    public final void setAction(String action){
        this.action=action;
    }

    public final String getRegistryName() {
        return registryName;
    }


    public final void setRegistryName(String registryName) {
        this.registryName = registryName;
    }


    public final EditRegistryRequest specify() {
        switch (action) {
            case CREATE:
                return new CreateRegistryRequest(registryName);
            case EMPTY:
                return new EmptyRegistryRequest(registryName);
            default:
                throw new IllegalStateException("Invalid action");
        }


    }

}
