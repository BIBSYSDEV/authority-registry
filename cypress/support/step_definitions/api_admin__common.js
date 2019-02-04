import {Given} from 'cypress-cucumber-preprocessor/steps';

Given('that the API admin user has a valid API key for API administration',
  () => {
    cy.log('-- api_admin__common.js --');

    cy.wrap('dummy').as('registryAdminApiKey');
  });
