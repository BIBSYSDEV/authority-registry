
// Scenario: An anonymous user views the metadata for a registry as RDF
//    Given that there is an existing populated entity registry with a schema
//    When an anonymous user dereferences the base URI for the registry specifying mediatypes:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    Then they see metadata related to the entity registry regarding:
//      | Metatata                |
//      | Available data profiles |

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When(
  'an anonymous user dereferences the base URI for the registry specifying mediatypes:',
  () => {
    cy.log('-- anonymous_user_view_entity_registry_metadata_RDF.js --');

    cy.get('@registryName').then((registryName) => {
      const createRegistryEndpoint = '/registry/' + registryName;
      cy.request({
        url: createRegistryEndpoint,
        method: 'GET',
        headers: {
          accept: 'application/rdf',
        },
      }).then((response) => {
        cy.wrap(response).as('registryMetadata');
      });
    });

  });

Then('they see metadata related to the entity registry regarding:', () => {
  cy.get('@registryMetadata').then((metadata) => {
  });
});
