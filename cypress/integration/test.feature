Feature: Test features
  Background:
    Given that there is an existing entity registry with a schema
#    And no cleanup
 
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

