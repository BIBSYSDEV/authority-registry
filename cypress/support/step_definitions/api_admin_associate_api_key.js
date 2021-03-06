//  Scenario: An API admin user associates an API key with the registry admin role for a registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user submits a new API key to replace the current valid API key
//    Then the API key is updated

import {When} from 'cypress-cucumber-preprocessor/steps';

When(
  'the API admin user submits a new API key to replace the current valid API key',
  () => {
    cy.log('-- api_admin_associate_api_key.js --');
    cy.get('@registryName').then((registryName) => {
      cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
        let addApikeyUrl = '/registry/' + registryName + '/apikey';
        cy.wrap('').as('newApiKey');
        cy.request({
          url: addApikeyUrl,
          method: 'POST',
          headers: {
            'api-key': apiAdminApiKey,
          },
        }).then((response) => {
          cy.wrap(response.body).as('newApiKey');
        });
      });
    });
  });

