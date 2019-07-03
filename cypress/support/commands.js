// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//

const SERVICE_UNAVAILABLE = 503;
const SEE_OTHER = 303;

const RECURSION_COUNT = 5;
const RECURSION_DELAY = 2000; // milliseconds
const VALID_SHACL_VALIDATION_FILE = 'validShaclValidationSchema.json';

Cypress.Commands.add('registryReady', (registryName) => {
  waitUntilRegistryIsReady(registryName, 0);
});

Cypress.Commands.add('deleteRegistry', (registryName, apiKey) => {
  deleteRegistry(registryName, apiKey);
});

Cypress.Commands.add('createEmptyRegistry',
  (registryName, apiKey, metadataFile) => {
    createRegistry(registryName, apiKey, metadataFile,
      VALID_SHACL_VALIDATION_FILE, false, 0);
  });

Cypress.Commands.add('createNonEmptyRegistry',
  (registryName, apiKey, metadataFile) => {
    createRegistry(registryName, apiKey, metadataFile,
      VALID_SHACL_VALIDATION_FILE, true, 0);
  });

Cypress.Commands.add('createEntity', (registryName, apiKey, dataFile) => {
  createEntity(registryName, apiKey, dataFile);
});

function waitUntilRegistryIsReady(registryName, count) {

  const statusUrl = '/registry/' + registryName + '/status';
  cy.log('waiting for registry to be ready...');
  cy.request({
    url: statusUrl,
    failOnStatusCode: false,
  }).then(function(response) {
    if (response.status === SEE_OTHER) {
      const newCount = count + 1;
      if (newCount < RECURSION_COUNT) {
        cy.wait(RECURSION_DELAY);
        waitUntilRegistryIsReady(registryName, newCount);
      }
    }
  });
}

// create registry
function createRegistry(registryName, apiAdminApiKey, metadataFile,
  validationSchemaFile, createEntity, count) {

  cy.log('creating registry...');

  cy.wait(12000);

  cy.log('Using apiKey ' + apiAdminApiKey);
  cy.fixture(metadataFile)
    .then(function(testSchema) {
      testSchema.id = registryName;
      const createUrl = '/registry';
      cy.log('trying to create registry');
      cy.request({
        url: createUrl,
        method: 'POST',
        body: testSchema,
        failOnStatusCode: false,
        headers: {
          'api-key': apiAdminApiKey,
          accept: 'application/json',
          'content-type': 'application/json',
        },
      }).then((response) => {
        if (response.status === SERVICE_UNAVAILABLE) {
          const newCount = count + 1;
          if (newCount < RECURSION_COUNT) {
            cy.wait(RECURSION_DELAY);
            createRegistry(registryName, apiAdminApiKey, metadataFile,
              createEntity, newCount);
          }
        } else {
          cy.log('api-key: ' + response.body.apiKey);
          cy.wrap(response.body.apiKey).as('registryAdminApiKey');

          cy.registryReady(registryName);

          setValidationSchema(registryName, apiAdminApiKey,
            validationSchemaFile);

          if (createEntity) {
            cy.log('creating test entity');
            cy.get('@registryAdminApiKey').then((registryAdminApiKey) => {
              const testDataFile = 'entityTestData.json';
              cy.createEntity(registryName, registryAdminApiKey, testDataFile);
            });
          }
        }
      });
    });
}

// add validation schema
function setValidationSchema(registryName, apiAdminApiKey,
  validationSchemaFile) {
  cy.log('setting validation schema for registry..');
  const putSchemaUrl = '/registry/' + registryName + '/schema';
  cy.fixture(validationSchemaFile).then((validationSchema) => {
    cy.request({
      url: putSchemaUrl,
      method: 'PUT',
      body: validationSchema,
      headers: {
        'api-key': apiAdminApiKey,
        accept: 'application/json',
        'content-type': 'application/json',
      },
    });
  });

}

// create entity in existing registry
function createEntity(registryName, apiKey, dataFile) {
  cy.log('creating entity...');

  const entityAddUrl = '/registry/' + registryName + '/entity';
  cy.fixture(dataFile).then((testData) => {
    cy.request({
      url: entityAddUrl,
      method: 'POST',
      body: testData,
      headers: {
        'api-key': apiKey,
        'content-type': 'application/json',
      },
    }).then((response) => {
      const entityId = response.body.id;
      cy.wrap(entityId).as('entityId');
    });
  });
}

function deleteRegistry(registryName, apiKey) {
  cy.log('deleting registry...');

  cy.log('api-key = ' + apiKey);

  const url = '/registry/' + registryName;
  cy.request({
    url: url,
    method: 'DELETE',
    headers: {
      'api-key': apiKey,
      'content-type': 'application/json',
    },
    failOnStatusCode: false,
  }).then((response) => {
    cy.log('delete registry status: ' + response.status);
  });
}
