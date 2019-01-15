

//* **********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.

// This is a great place to put global configuration and
// behavior that modifies Cypress.

// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.

// You can read more here:
// https://on.cypress.io/configuration
//* **********************************************************

// Import commands.js using ES2015 syntax:
import './commands';

// Alternatively you can use CommonJS syntax:
// require('./commands')

beforeEach(function(){
  let uuid = require('uuid');
  let whoami = Cypress.env('whoami');
  if (whoami === undefined){
    whoami = 'test_';
  }

  let randomRegistryName = whoami + uuid.v4();
  cy.wrap(randomRegistryName).as('registryName');

  let apiKey = Cypress.env('apiKey');
  if (apiKey === undefined){
    apiKey = 'testApiAdminApiKey';
  }

  cy.wrap(true).as('cleanUp');
  cy.wrap(apiKey).as('apiAdminApiKey');
});


afterEach(function(){
  cy.get('@cleanUp').then((doCleanUp) => {
    if (doCleanUp){
      cy.get('@registryName').then(function(registryName) {
        cy.log('removing DynamoDB table ' + registryName);

        cy.registryReady(registryName);

        cy.get('@registryAdminApiKey').then(function(apiKey) {

          cy.deleteRegistry(registryName, apiKey);
        });
      });
    }
  });
});
