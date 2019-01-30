import {Given} from 'cypress-cucumber-preprocessor/steps';

Given('that there is an existing entity registry with a schema', () => {
  cy.log('creating empty registry');
  createTestRegistry(false);
});

Given('that there is an existing, empty entity registry with a schema', () => {
  cy.log('creating empty registry');
  createTestRegistry(false);
});

Given('that there is an entity in the registry', () => {
  cy.log('creating entity');
  createTestEntity();
});

Given('that there is an existing entity in the registry', () => {
  cy.log('creating entity');
  createTestEntity();
});

Given('that there is an existing, populated entity registry with a schema', () => {
  cy.log('creating populated registry');
  createTestRegistry(true);
});

Given('that there is an existing, populated entity registry with a schema and registered registry API keys', () => {
  cy.log('creating populated registry');
  createTestRegistry(true);
});

function createTestRegistry(createEntity){

  cy.log('-- entity_registry_common.js --');
  cy.get('@registryName').then(function(registryName) {
    cy.log('create Entity? ' + createEntity);
    cy.log('Creating schema with name ' + registryName);
    cy.get('@apiAdminApiKey').then(function(apiKey) {
      // create new test registry metadata
      let testMetadataFile = 'registryTestMetadata.json';
      if (createEntity){
        cy.createNonEmptyRegistry(registryName, apiKey, testMetadataFile);
      } else {
        cy.createEmptyRegistry(registryName, apiKey, testMetadataFile);
      }
    });
  });
}

function createTestEntity() {
  cy.get('@registryName').then(function(registryName) {
    cy.get('@apiAdminApiKey').then(function(apiKey) {
      let dataFile = 'entityTestData.json';
      cy.createEntity(registryName, apiKey, dataFile);
    });
  });
}
