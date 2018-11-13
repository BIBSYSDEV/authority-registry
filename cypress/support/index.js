// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

beforeEach(function(){
	let uuid = require('uuid');
	let randomRegistryName = uuid.v4();
	cy.wrap(randomRegistryName).as('registryName');
	let randomEntityId = uuid.v4();
	cy.wrap(randomEntityId).as('entityId');
	cy.wrap('fac116dd-1b6d-4c95-85ed-b90dd51a480e').as('apiAdminKey');
})

afterEach(function(){
	cy.get("@registryName").then((registryName) => {
		cy.log("removing DynamoDB table " + registryName)

		let emptyUrl = "/registry/" + registryName + "/empty"
		cy.request({
			url: emptyUrl,
			method: 'DELETE',
			headers: {
				Authorization: 'Token API_admin_token',
				'content-type': 'application/json'
			},
			failOnStatusCode: false
		}).then((response) => {})
		
		let deleteUrl = "/registry/" + registryName
		cy.request({
			url: deleteUrl,
			method: 'DELETE',
			headers: {
				Authorization: 'Token API_admin_token',
				'content-type': 'application/json'
			},
			failOnStatusCode: false
		}).then((response) => {})
	})
})