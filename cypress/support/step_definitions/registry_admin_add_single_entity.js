// Scenario: An registry admin user adds a single entity to a registry
// Given that the registry admin user has a valid API key for registry administration
// And that there is an existing entity registry with a schema
// When the registry admin user submits the API key with a request to create a new entity with properly formatted data
// Then the entity is created

import {Then, When} from 'cypress-cucumber-preprocessor/steps';
import * as HttpStatus from 'http-status-codes';

When(
  'the registry admin user submits the API key with a request to create a new entity with properly formatted data',
  () => {
    cy.log('-- registry_admin_add_single_entity.js --');
    cy.get('@registryName').then((registryName) => {
      const createEntityUrl = '/registry/' + registryName + '/entity';
      cy.fixture('entityTestData.json')
        .then(testData => {
          cy.get('@registryAdminApiKey').then((apiKey) => {
            cy.request({
              url: createEntityUrl,
              method: 'POST',
              headers: {
                'api-key': apiKey,
                'content-type': 'application/json',
              },
              body: testData,
            }).then((response) => {
              // test return from create
              cy.wrap(response.body.id).as('entityId');
            });
          });
        });
    });
  });

Then('the entity is created', () => {
  cy.get('@registryName').then(registryName => {
    cy.get('@entityId').then(entityId => {
      cy.get('@registryAdminApiKey').then(apiKey => {
        cy.request({
          url: '/registry/' + registryName + '/entity/' + entityId,
          headers: {
            'api-key': apiKey,
          },
        }).then((response) => {
          expect(response.status).to.be.equals(HttpStatus.CREATED);
        });
      });
    });
  });
});
