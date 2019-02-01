//  Scenario: An API admin user updates an existing, empty entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
//    Then the entity registry is updated

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When(
  'the API admin user uses the API key and submits a request to update the validation schema of the entity registry',
  () => {
    cy.log('-- api_admin_update_empty_registry_schema.js --');
    cy.get('@registryName').then((registryName) => {
      cy.get('@apiAdminApiKey').then((apiKey) => {
        const registryGetUrl = '/registry/' + registryName + '/schema';
        cy.registryReady(registryName);
        cy.request({
          url: registryGetUrl,
          method: 'GET',
          headers: {
            'api-key': apiKey,
          },
        }).then((response) => {
          cy.fixture('validShaclValidationSchema.json').then(
            schemaObject => {
              const responseSchemaObj = JSON.parse(response.body.schema);
              expect(responseSchemaObj).to.deep.equal(schemaObject);
            });
        });

        const registryUpdateUrl = '/registry/' + registryName + '/schema';
        cy.fixture('alternativeValidShaclValidationSchema.json').then(
          altSchemaObj => {
            const schemaString = JSON.stringify(altSchemaObj);
            updateSchema(registryUpdateUrl, apiKey, schemaString);

          });

      });
    });
  });

Then('the entity registry is updated', () => {
  cy.get('@updateSchemaResponse').then((response) => {
    expect(response.status).to.equal(200);
  });
  cy.get('@registryName').then((registryName) => {
    const registryGetUrl = '/registry/' + registryName + '/schema';
    cy.get('@apiAdminApiKey').then((apiKey) => {
      cy.registryReady(registryName);
      cy.request({
        url: registryGetUrl,
        method: 'GET',
        headers: {
          'api-key': apiKey,
        },
      }).then((response) => {
        cy.fixture('alternativeValidShaclValidationSchema.json')
          .then(
            altSchemaObj => {
              const responseSchemaObj = JSON.parse(response.body.schema);
              expect(responseSchemaObj).to.deep.equal(altSchemaObj);
            });

      });
    });
  });
});

function updateSchema(registryUpdateUrl, apiKey, schemaString) {

  cy.request({
    url: registryUpdateUrl,
    method: 'PUT',
    headers: {
      'api-key': apiKey,
      'content-type': 'application/json',
    },
    body: schemaString,
    failOnStatusCode: false,
  }).then((response) => {
    cy.wrap(response).as('updateSchemaResponse');
  });

}
