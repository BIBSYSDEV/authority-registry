Feature: Registry admin features

  Background:
    Given that the registry admin user is authenticated
    And that there is an existing entity registry with a schema

  Scenario: An registry admin user adds a single entity to a registry
    When the registry admin user requests the creation of a new entity with properly formatted data
    Then the entity is created

  Scenario: A registry admin user populates a registry
    And that the registry admin user has a set of properly schema-formatted data
    When the registry admin user bulk uploads the data to the entity registry
    Then the data is available in the entity registry
