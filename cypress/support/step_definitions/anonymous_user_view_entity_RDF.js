//  Scenario: An anonymous user views an entity specifying an RDF serialization
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    Then anonymous user can view the data in the given serialization

import {Then} from 'cypress-cucumber-preprocessor/steps';

// implemented in anonymous_user_view_entity_MARC_HTML_RDF.js
