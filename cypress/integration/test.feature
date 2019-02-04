Feature: Test features
  Background:
    Given that there is an existing, populated entity registry with a schema

#    And no cleanup
 
  Scenario: An anonymous user views the metadata for a registry as RDF
    When an anonymous user dereferences the base URI for the registry specifying mediatypes:
     | application/ld+json     |
     | application/n-triples   |
     | application/rdf+xml     |
     | application/turtle      |
     | application/json        |
     | application/rdf         |
    Then they see metadata related to the entity registry regarding:
      | Metatata                |
      | Available data profiles |

  Scenario: An API admin user updates an existing, empty entity registry
    Given that the API admin user has a valid API key for API administration
    And that there is an existing, empty entity registry with a schema
    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
    Then the entity registry is updated