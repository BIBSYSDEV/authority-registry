import {Given} from 'cypress-cucumber-preprocessor/steps';

Given('no cleanup', () => {
  cy.log('-- api_admin__common.js --');
  cy.wrap(false).as('cleanUp');
});
