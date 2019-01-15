//  Scenario: An API admin user updates an existing, empty entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
//    Then the entity registry is updated

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the API admin user uses the API key and submits a request to update the validation schema of the entity registry', () => {
  cy.log('-- api_admin_update_empty_registry_schema.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@apiAdminApiKey').then((apiKey) => {

      let registryGetUrl = '/registry/' + registryName + '/schema';
      cy.registryReady(registryName);
      cy.request({
        url: registryGetUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.body.schema).to.equal('testSchema');
      });

      let registryUpdateUrl = '/registry/' + registryName + '/schema';
      let updatedSchema = 'updatedTestSchema';
      cy.request({
        url: registryUpdateUrl,
        method: 'PUT',
        headers: {
          'api-key': apiKey,
          'content-type': 'application/json',
        },
        body: updatedSchema,
        failOnStatusCode: false,
      }).then((response) => {
        cy.wrap(response).as('updateSchemaResponse');
      });
    });
  });
});

Then('the entity registry is updated', () => {
  cy.get('@updateSchemaResponse').then((response) => {
    expect(response.status).to.equal(200);
  });
  cy.get('@registryName').then((registryName) => {
    let registryGetUrl = '/registry/' + registryName + '/schema';
    cy.get('@apiAdminApiKey').then((apiKey) => {
      cy.registryReady(registryName);
      cy.request({
        url: registryGetUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.body.schema).to.equal('updatedTestSchema');
      });
    });
  });
});
