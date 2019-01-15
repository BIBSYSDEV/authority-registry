import {Given} from 'cypress-cucumber-preprocessor/steps';

Given('that the registry admin user has a valid API key for registry administration', () => {
  cy.log('-- registry_admin__common.js --');

  cy.wrap('dummy').as('registryAdminApiKey');
});
