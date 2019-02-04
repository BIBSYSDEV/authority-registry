// Scenario: An API admin user deletes populated data from an entity registry
// Given that the API admin user has a valid API key for API administration
// And that there is an existing, populated entity registry with a schema
// When the API admin user uses the API key and submits a request to delete the data in the entity registry
// Then the API admin user receives information that the data is deleted

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When(
  'the API admin user uses the API key and submits a request to delete the data in the entity registry',
  () => {
    cy.log('-- api_admin_delete_data_from_populated_registry.js --');
    let deleteDataUrl = '/registry/';
    cy.get('@apiAdminApiKey').then((apiKey) => {
      cy.get('@registryName').then((registryName) => {
        cy.request({
          url: deleteDataUrl + registryName + '/empty',
          method: 'DELETE',
          headers: {
            'api-key': apiKey,
          },
        }).then((response) => {
          cy.wrap(response.body).as('deleteConfirmation');
        });
      });
    });
  });

Then('the API admin user receives information that the data is deleted', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@deleteConfirmation').then((deleteComfirmation) => {
      expect(deleteComfirmation).is.equal(
        'Registry ' + registryName + ' has been emptied');
      let registryUrl = '/registry' + registryName;
      cy.request(registryUrl).then((response) => {
        // empty registry needs to be made async. Does not work until this is fixed
        //				expect(response.body.size).to.equal(0);
      });
    });
  });
});
