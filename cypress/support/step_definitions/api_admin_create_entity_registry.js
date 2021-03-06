// Scenario: An API admin user creates a new entity registry
// Given that the API admin user has a valid API key for API administration
// When the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:
// | Registry name              |
// | Registry admin users       |
// | Registry validation schema |
// Then an entity registry that accepts only valid data is created

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When(
  'the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:',
  () => {
    cy.log('-- api_admin_create_entity_registry.js --');
    cy.get('@registryName').then((registryName) => {
      cy.get('@apiAdminApiKey').then((apiKey) => {
        cy.createEmptyRegistry(registryName, apiKey,
          'registryTestMetadata.json');
      });
    });
  });

Then('an entity registry that accepts only valid data is created', () => {

  cy.get('@registryName').then((registryName) => {
    cy.registryReady(registryName);

    cy.get('@apiAdminApiKey').then((apiKey) => {
      let getRegistryUrl = '/registry/' + registryName;
      cy.request({
        url: getRegistryUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.status).to.equal(200);
      });
    });
  });
});
