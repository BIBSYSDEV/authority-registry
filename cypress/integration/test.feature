Feature: Test features

#    And no cleanup

  Scenario: An API admin user updates an existing, empty entity registry
    Given that the API admin user has a valid API key for API administration
    And that there is an existing, empty entity registry with a schema
    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
    Then the entity registry is updated