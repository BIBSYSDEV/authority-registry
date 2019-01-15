//  Scenario: An API admin user updates the entity registry metadata
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user changes the metadata for the entity registry
//    Then the metadata for the entity registry is updated

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the API admin user changes the metadata for the entity registry', () => {
  cy.log('-- api_admin_update_registry_metadata_.js --');

  cy.get('@registryName').then((registryName) => {
    cy.get('@registryAdminApiKey').then((apiKey) => {

      let registryGetUrl = '/registry/' + registryName;
      cy.request({
        url: registryGetUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.body.metadata.description).to.equals('descriptionValue');
      });

      let registryUpdateUrl = '/registry/' + registryName;
      cy.fixture('registryTestMetadataUpdated.json').then((updatedSchema) => {
        updatedSchema.id = registryName;
        cy.log('description: ' + updatedSchema.metadata.description);
        cy.request({
          url: registryUpdateUrl,
          method: 'PUT',
          body: updatedSchema,
          headers: {
            'api-key': apiKey,
            'Content-Type': 'application/json',
          },
        });
      });
    });
  });
});

Then('the metadata for the entity registry is updated', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@registryAdminApiKey').then((apiKey) => {
      cy.registryReady(registryName);
      let registryGetUrl = '/registry/' + registryName;
      cy.request({
        url: registryGetUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        expect(response.body.metadata.description).to.equals('updatedDescriptionValue');
      });
    });
  });
});
