Feature: Registry admin features

  Background:
    Given that the registry admin user has a valid API key for registry administration
    And that there is an existing entity registry with a schema

  Scenario: An registry admin user adds a single entity to a registry

    When the registry admin user submits the API key with a request to create a new entity with properly formatted data
    Then the entity is created
 
  Scenario: A registry admin user populates a registry
    And that the registry admin user has a set of properly schema-formatted data
    When the registry admin user submits an API key and a request to bulk upload the data to the entity registry
    Then the data is available in the entity registry

  Scenario: A registry admin adds registry admin API keys to an existing, populated entity registry
    And that the entity registry is populated
    When the API admin user adds registry admin API keys to the entity registry
    Then the users with the API keys can access the entity registry
