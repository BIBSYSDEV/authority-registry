//  Scenario: A registry admin user populates a registry
//    Given that the registry admin user has a valid API key for registry administration
//    And that there is an existing entity registry with a schema
//    And that the registry admin user has a set of properly schema-formatted data
//    When the registry admin user submits an API key and a request to bulk upload the data to the entity registry
//    Then the data is available in the entity registry

import {Given, Then, When} from 'cypress-cucumber-preprocessor/steps';

Given('that the registry admin user has a set of properly schema-formatted data', () => {
  cy.log('-- registry_admin_populate_registry.js --');
  cy.fixture('testDataBulk.json').as('bulkUpload');
  // test against schema here?
});

When('the registry admin user submits an API key and a request to bulk upload the data to the entity registry', () => {
  cy.get('@bulkUpload').then((bulkUpload) => {
    cy.get('@registryName').then((registryName) => {
      let bulkUploadUrl = '/registry/' + registryName + '/upload';
      cy.get('@apiAdminApiKey').then((apiKey) => {
        cy.request({
          url: bulkUploadUrl,
          method: 'POST',
          headers: {
            'api-key': apiKey,
            'content-type': 'application/json',
          },
          body: bulkUpload,
        }).then((response) => {
          cy.wrap(response.body).as('uploadResponse');
        });
      });
    });
  });
});

Then('the data is available in the entity registry', () => {
  // get all added entities one at a time to see if they have been uploaded
  cy.get('@registryName').then((registryName) => {
    cy.get('@uploadResponse').then((response) => {
      response.forEach((entity, index) => {
        cy.log('id = ' + entity.id);
        const url = '/registry/' + registryName + '/entity/' + entity.id;
        cy.request({
          url: url,
          headers: {
            accept: 'application/json',
          },
        });
      });
    });
  });
});
