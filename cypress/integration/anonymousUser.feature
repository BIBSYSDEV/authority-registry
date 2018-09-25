Feature: Anonymous user features

  Background:
    Given that there is an existing entity registry with a schema

  Scenario: An anonymous user views an entity without specifying a format
    Given that there is an entity in the registry
    When the anonymous user requests the entity
    Then anonymous user can view the entity's data in the native database format

  Scenario: An anonymous user views API information
    When an anonymous user requests the OpenAPI documentation
    Then the OpenAPI documentation is returned

  Scenario: An anonymous user submits a request to see if a resource is modified
    Given that there is an existing entity in the registry
    When the anonymous user requests the entity
    Then the response contains an ETag and a Last-Modified header
    
  Scenario: An anonymous user views an entity specifying an RDF serialization
    Given that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value:
      | application/ld+json     |
      | application/n-triples   |
      | application/rdf+xml     |
      | application/turtle      |
      | application/json        |
      | application/rdf         |
    Then anonymous user can view the data in the given serialization

  Scenario: An anonymous user views an entity as HTML
    Given that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value text/html
    Then anonymous user can view the data in the given format

  Scenario: An anonymous user views an entity specifying a specific MARC format
    Given that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value:
      | application/marcxml+xml |
      | application/marc        |
      | application/mads+xml    |
      | application/marcxml     |
    Then anonymous user can view the data in the given MARC format
  
  @NotMVP
  Scenario: An anonymous user views an entity specifying a specific RDF serialization Given a specific profile
    Given that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value:
      | application/ld+json     |
      | application/n-triples   |
      | application/rdf+xml     |
      | application/turtle      |
      | application/json        |
      | application/rdf         |
    Given specifies an Accept-schema header with a value:
      | native-uri   |
      | skos-uri     |
      | bibframe-uri |
    Then anonymous user can view the data in the serialization and profile requested
