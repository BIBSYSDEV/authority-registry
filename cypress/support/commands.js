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
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add("registryReady", (registryName) => {
	waitUntilRegistryIsReady(registryName, 0);
})

Cypress.Commands.add("deleteRegistry", (registryName, apiKey) => {
	deleteRegistry(registryName, apiKey);
})

Cypress.Commands.add("createEmptyRegistry", (registryName, apiKey, metadataFile) => {
	createRegistry(registryName, apiKey, metadataFile, false);
})

Cypress.Commands.add("createNonEmptyRegistry", (registryName, apiKey, metadataFile) => {
	createRegistry(registryName, apiKey, metadataFile, true);
})

Cypress.Commands.add("createEntity", (registryName, apiKey, dataFile) => {
	createEntity(registryName, apiKey, dataFile);
})

function waitUntilRegistryIsReady(registryName, count){

	let statusUrl = '/registry/' + registryName + '/status'
	cy.log('waiting for registry to be ready...')
	cy.request({
		url: statusUrl,
		failOnStatusCode: false
	}).then(function (response) {
		if(response.status === 303){
			const newCount = count + 1;
			if(newCount < 5){
				cy.wait(2000)
				waitUntilRegistryIsReady(registryName, newCount)
			}
		}
	})
}

// create registry
function createRegistry(registryName, apiAdminApiKey, metadataFile, createEntity) {
	cy.log('creating registry...')
	
	cy.log('Using apiKey ' + apiAdminApiKey)
	cy.fixture(metadataFile)
	.then(function (testSchema) {
		testSchema.id = registryName;
		let createUrl = '/registry';
		cy.request({
			url: createUrl,
			method: 'POST',
			body: testSchema, 
			headers: {
				'api-key': apiAdminApiKey,
				"content-type": "application/json"
			}
		}).then((response) => {
			cy.log('api-key: ' + response.body.apiKey)
			cy.wrap(response.body.apiKey).as('registryAdminApiKey')

			cy.registryReady(registryName)
			
			if(createEntity){
				cy.log('creating test entity')
				cy.get('@registryAdminApiKey').then(function (registryAdminApiKey) {
					let testDataFile = 'entityTestData.json'
					cy.createEntity(registryName, registryAdminApiKey, testDataFile)
				})
			}
		})
	})
}

// create entity in existing registry
function createEntity(registryName, apiKey, dataFile) {
	cy.log('creating entity...')
	
	let entityAddUrl = '/registry/' + registryName + '/entity';
	cy.fixture(dataFile) // add testData to registry
	.then(function (testData) {
		cy.request({
			url: entityAddUrl,
			method: 'POST',
			body: testData,
			headers: {
				'api-key': apiKey,
				'content-type': 'application/json'
			}
		}).then(function (response) {
			let entityId = response.body.entityId
			cy.wrap(entityId).as('entityId');
		})
	})
}

function deleteRegistry(registryName, apiKey){
	cy.log('deleting registry...')
	
	cy.log('api-key = ' + apiKey)
	let emptyUrl = '/registry/' + registryName + '/empty'
	cy.request({
		url: emptyUrl,
		method: 'DELETE',
		headers: {
			'api-key': apiKey,
			'content-type': 'application/json'
		},
		failOnStatusCode: false
	}).then(function (response) {
		cy.log('empty registry status: ' + response.status)

		let url = '/registry/' + registryName
		cy.request({
			url: url,
			method: 'DELETE',
			headers: {
				'api-key': apiKey,
				'content-type': 'application/json'
			},
			failOnStatusCode: false
		}).then(function (response) {
			cy.log('delete registry status: ' + response.status)
		})
	})
}