//  Scenario: An anonymous user views an entity as HTML
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value text/html
//    Then anonymous user can view the data in the given format

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When(/the anonymous user requests the entity specifying an Accept header with value text\/html/, () => {
  cy.log('-- anonymous_user_view_entity_HTML.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
      cy.visit(getEntityUrl);
    });
  });
});

Then('anonymous user can view the data in the given format', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
      cy.visit(getEntityUrl);
      cy.get('li[data-automation-id="@id"]').contains(
          'http://authority-registry/validation/__THIS_DOCUMENT__a');
      cy.get('li[data-automation-id="@type"]').contains('unit:Concept');
      cy.get('li[data-automation-id=preferredLabel]').contains('norskLabel');
    });
  });
});
