//  Scenario: An API admin user attempts to delete an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user uses the API key and submits a request to delete the entity registry
//    Then the API admin user receives information that they cannot delete the entity registry until the populated data is deleted

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the API admin user uses the API key and submits a request to delete the entity registry', () => {
  cy.log('-- api_admin_delete_populated_registry.js --');
  cy.get('@registryName').then((registryName) => {

    cy.get('@apiAdminApiKey').then((apiKey) => {
      cy.log('apiKey = ' + apiKey);
      let url = '/registry/' + registryName;
      cy.request({
        url: url,
        method: 'DELETE',
        headers: {
          'api-key': apiKey,
        },
        failOnStatusCode: false,
      }).then(function(response) {
        cy.wrap(response).as('errorResponse');
      });
    });
  });
});

Then('the API admin user receives information that they cannot delete the entity registry until the populated data is deleted', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@errorResponse').then((errorResponse) => {
      expect(errorResponse.status).to.equal(405);
      expect(errorResponse.body).to.equal('Registry with name ' + registryName + ' is not empty');
    });
  });
});
