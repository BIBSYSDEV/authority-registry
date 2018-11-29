Feature: Test features
  Background:
    Given that there is an existing entity registry with a schema
#    And no cleanup
 
  Scenario: An anonymous user views an entity without specifying a format
    And that there is an entity in the registry
    When the anonymous user requests the entity
    Then anonymous user can view the entity's data in the native database format

