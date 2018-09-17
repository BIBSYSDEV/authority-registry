package no.bibsys.handlers;

public class CreateRegistryRequest {

  private String registryName;

  public CreateRegistryRequest() {
  }

  ;


  public CreateRegistryRequest(String registryName) {
    this.registryName = registryName;
  }



  public String getRegistryName() {
    return registryName;
  }

  public void setRegistryName(String registryName) {
    this.registryName = registryName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreateRegistryRequest)) {
      return false;
    }

    CreateRegistryRequest that = (CreateRegistryRequest) o;

    return registryName != null ? registryName.equals(that.registryName)
        : that.registryName == null;
  }

  @Override
  public int hashCode() {
    return registryName != null ? registryName.hashCode() : 0;
  }


}
