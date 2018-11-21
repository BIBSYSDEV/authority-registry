Feature: Registry admin features

  Background:
    Given that the registry admin user has a valid API key for registry administration
    And that there is an existing entity registry with a schema

  Scenario: An registry admin user adds a single entity to a registry

    When the registry admin user submits the API key with a request to create a new entity with properly formatted data
    Then the entity is created
