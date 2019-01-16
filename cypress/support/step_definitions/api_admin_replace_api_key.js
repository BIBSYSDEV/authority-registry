//  Scenario: An API admin user replaces an API key with the registry admin role for a registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user requests a new API key to replace the current valid API key
//    Then the API key is updated
//    And the user receives the updated API key

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the API admin user requests a new API key to replace the current valid API key', () => {
  cy.log('-- api_admin_replace_api_key.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@registryAdminApiKey').then((registryAdminApiKey) => {
      cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
        let url = '/registry/' + registryName + '/apikey';
        cy.request({
          url: url,
          method: 'PUT',
          body: registryAdminApiKey,
          headers: {
            'api-key': apiAdminApiKey,
          },
        }).then((response) => {
          cy.wrap(response.body).as('newApiKey');
        });
      });
    });
  });
});

Then('the API key is updated', () => {
  cy.get('@registryName').then((registryName) => {
    cy.fixture('entityTestData').then((testData) => {
      cy.get('@registryAdminApiKey').then((registryAdminApiKey) => {
        cy.log('api-key = ' + registryAdminApiKey);
        let url = '/registry/' + registryName + '/entity';
        cy.request({
          url: url,
          method: 'POST',
          body: testData,
          failOnStatusCode: false,
          headers: {
            'api-key': registryAdminApiKey,
            'content-type': 'application/json',
          },
        }).then((response) => {
          cy.expect(response.status).to.equal(403);
        });
      });
      cy.get('@newApiKey').then((newRegistryAdminApiKey) => {
        cy.log('api-key = ' + newRegistryAdminApiKey);
        let url = '/registry/' + registryName + '/entity';
        cy.request({
          url: url,
          method: 'POST',
          body: testData,
          failOnStatusCode: false,
          headers: {
            'api-key': newRegistryAdminApiKey,
            'content-type': 'application/json',
          },
        }).then((response) => {
          cy.expect(response.status).to.equal(200);
        });
      });
    });
  });
});

Then('the user receives the updated API key', () => {
  cy.get('@newApiKey').then((newApiKey) => {
    cy.wrap(newApiKey).as('registryAdminApiKey'); // using the new apikey when cleaning up test registries
  });
});
