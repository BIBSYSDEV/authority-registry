//  Scenario: An API admin user provides a valid API key
//    Given that an API admin user has a valid API key for API administration
//    When they submit the API key
//    Then they can access the administration APIs

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('they submit the API key', () => {
  cy.log('-- api_admin_user_provides_api_key.js --');
  cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
    cy.get('@registryName').then((registryName) => {
      // create new test registry metadata
      cy.fixture('registryTestMetadata.json')
      .then((testSchema) => {
        testSchema.id = registryName;
        let createUrl = '/registry';
        cy.request({
          url: createUrl,
          method: 'POST',
          body: testSchema,
          headers: {
            'api-key': apiAdminApiKey,
            'content-type': 'application/json',
          },
        }).then((response) => {
          cy.wrap(response.status).as('responseStatus');
          cy.wrap(response.body.apiKey).as('registryAdminApiKey');
        });
      });
    });
  });
});

Then('they can access the administration APIs', () => {
  cy.get('@responseStatus').then((status) => {
    expect(status).to.not.equal(403);
  });
});
