// Scenario: An API admin user deletes an existing, empty entity registry
// Given that the API admin user has a valid API key for API administration
// And that there is an existing, empty entity registry with a schema
// When the API admin user uses the API key and requests deletion of an entity registry
// Then the empty entity registry is deleted

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the API admin user uses the API key and requests deletion of an entity registry', () => {
  cy.log('-- api_admin_delete_empty_registry.js --');
  // delete empty registry
  cy.get('@registryName').then((registryName) => {
    cy.get('@registryAdminApiKey').then((apiKey) => {
      let url = '/registry/' + registryName;
      cy.request({
        url: url,
        method: 'DELETE',
        headers: {
          'api-key': apiKey,
          'content-type': 'application/json',
        },
      }).then(function(response) {
      });
    });
  });
});

Then('the empty entity registry is deleted', () => {
  // call registry
  cy.get('@registryName').then((registryName) => {
    cy.get('@apiAdminApiKey').then((apiKey) => {
      let registryHeartbeatUrl = '/registry/' + registryName;
      cy.request({
        url: registryHeartbeatUrl,
        method: 'GET',
        failOnStatusCode: false,
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.status).equals(404);
      });
    });
  });
});
