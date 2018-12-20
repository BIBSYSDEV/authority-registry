Feature: Test features
  Background:
    Given that there is an existing entity registry with a schema
#    And no cleanup
 
  Scenario: An anonymous user views an entity as HTML
    And that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value text/html
    Then anonymous user can view the data in the given format

