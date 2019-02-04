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
      | Metadata                |
      | Available data profiles |
