Feature: API admin features

  Background:
    Given that the API admin user is authenticated

  Scenario: An API admin user creates a new entity registry
    When the API admin user provides a properly formatted create-entity-registry-request providing information about:
      | Registry name              |
      | Registry admin users       |
      | Registry validation schema |
    Then an entity registry that accepts only valid data is created

  Scenario: An API admin user deletes an existing, empty entity registry
    And that there is an existing, empty entity registry with a schema
    When the API admin user request deletion of an entity registry
    Then the empty entity registry is deleted

  Scenario: An API admin user updates an existing, empty entity registry
    And that there is an existing, empty entity registry with a schema
    When the API admin user updates the metadata and validation schemas of the entity registry
    Then the entity registry is updated

  Scenario: An API admin user attempts to delete an existing, populated entity registry
    And that there is an existing, populated entity registry with a schema
    When the API admin user attempts to delete the entity registry
    Then the API admin user receives information that they cannot delete the entity registry until the populated data is deleted

  Scenario: An API admin user attempts to update an existing, populated entity registry
    And that there is an existing, populated entity registry with a schema
    When the API admin user attempts to update the entity registry
    Then the API admin user receives information that they cannot update the entity registry until the populated data is deleted

  Scenario: An API admin user deletes populated data from an entity registry
    And that there is an existing, populated entity registry with a schema
    When the API admin user deletes the data in the entity registry
    Then the API admin user receives information that the data is deleted