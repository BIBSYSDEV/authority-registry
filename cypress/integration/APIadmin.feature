Feature: API admin features

  Background:
    Given that the API admin user has a valid API key for API administration

  Scenario: An API admin user provides a valid API key
    When they submit the API key
    Then they can access the administration APIs

  Scenario: An API admin user creates a new entity registry
    When the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:
      | Registry name              |
      | Registry admin users       |
      | Registry validation schema |
    Then an entity registry that accepts only valid data is created

  Scenario: An API admin user deletes an existing, empty entity registry
    And that there is an existing, empty entity registry with a schema
    When the API admin user uses the API key and requests deletion of an entity registry
    Then the empty entity registry is deleted

  Scenario: An API admin user updates an existing, empty entity registry
    And that there is an existing, empty entity registry with a schema
    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
    Then the entity registry is updated

  Scenario: An API admin user attempts to delete an existing, populated entity registry
    And that there is an existing, populated entity registry with a schema
    When the API admin user uses the API key and submits a request to delete the entity registry
    Then the API admin user receives information that they cannot delete the entity registry until the populated data is deleted

  Scenario: An API admin user attempts to update the validation schema of an existing, populated entity registry
    And that there is an existing, populated entity registry with a schema
    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
    Then the API admin user receives information that they cannot update the entity registry validation schema until the populated data is deleted

  Scenario: An API admin user updates the entity registry metadata
    And that there is an existing, populated entity registry with a schema
    When the API admin user changes the metadata for the entity registry
    Then the metadata for the entity registry is updated

#  Scenario: An API admin user deletes populated data from an entity registry
#    And that there is an existing, populated entity registry with a schema
#    When the API admin user uses the API key and submits a request to delete the data in the entity registry
#    Then the API admin user receives information that the data is deleted

#  Scenario: An API admin user associates an API key with the registry admin role for a registry
#    And that there is an existing, populated entity registry with a schema
#    When the API admin user submits a new API key to replace the current valid API key
#    Then the API key is updated

#  Scenario: An API admin user removes registry admin API keys from an existing, populated entity registry
#    And that there is an existing, populated entity registry with a schema and registered registry API keys
#    When the API admin user removes registry admin API keys from the entity registry
#    Then the API keys no longer provide access to the entity registry

