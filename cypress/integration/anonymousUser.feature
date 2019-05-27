Feature: Anonymous user features

  Background:
    Given that there is an existing entity registry with a schema

  Scenario: An anonymous user views an entity without specifying a format
    And that there is an entity in the registry
    When the anonymous user requests the entity
    Then anonymous user can view the entitys data in the native database format

  Scenario: An anonymous user views API information
    When an anonymous user requests the OpenAPI documentation
    Then the OpenAPI documentation is returned

  Scenario: An anonymous user submits a request to see if a resource is modified
    And that there is an existing entity in the registry
    When the anonymous user requests the entity
    Then the response contains an ETag and a Last-Modified header

  Scenario: An anonymous user views an entity specifying an RDF serialization
    And that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value:
      | application/ld+json   |
      | application/n-triples |
      | application/rdf+xml   |
      | application/turtle    |
      | application/json      |
      | application/rdf       |
    Then anonymous user can view the data in the given serialization

  Scenario: An anonymous user views an entity as HTML
    And that there is an entity in the registry
    When the anonymous user requests the entity specifying an Accept header with value text/html
    Then anonymous user can view the data in the given format

  Scenario: An anonymous user views an entity specifying a specific format
    And that there is an entity in the registry
    When the anonymous user requests the entity with format:

      | application/marcxml+xml |
      | application/marc        |
      | application/marcxml     |
    Then anonymous user can view the data in the format

  Scenario: An anonymous user views the metadata for a registry as HTML
    When an anonymous user dereferences the base URI for the registry specifying mediatype text/html
    Then they see metadata related to the entity registry regarding:
      | Registry name                    |
      | Registry type                    |
      | Publisher                        |
      | License for the data             |
      | Owner organisation               |
      | Participating organisations      |
      | Languages used in dataset        |
      | Creation date                    |
      | Modification date                |
      | Relations to other data sets     |
      | Location of APIs                 |
      | Example resources                |
      | Base URI for dataset             |
      | Location of SPARQL endpoint      |
      | Description of available formats |

  Scenario: An anonymous user views the metadata for a registry as RDF
    And that there is an entity in the registry
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
