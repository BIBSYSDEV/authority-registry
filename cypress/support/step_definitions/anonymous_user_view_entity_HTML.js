//  Scenario: An anonymous user views an entity as HTML
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value text/html
//    Then anonymous user can view the data in the given format

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

const TEXT_HTML = 'text/html';

When(/the anonymous user requests the entity specifying an Accept header with value text\/html/, () => {
  cy.log('-- anonymous_user_view_entity_HTML.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
      cy.request({
        url: getEntityUrl,
        headers: {
          Accept: TEXT_HTML,
        },
      }).then((response) => {
        cy.wrap(response).as('htmlResponse');
      });
    });
  });
});

Then('anonymous user can view the data in the given format', () => {
  cy.get('@htmlResponse').then((response) => {
    assert.include(response.body, 'html-kode');
  });
});
