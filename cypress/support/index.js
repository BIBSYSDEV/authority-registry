//***********************************************************
//This example support/index.js is processed and
//loaded automatically before your test files.

//This is a great place to put global configuration and
//behavior that modifies Cypress.

//You can change the location of this file or turn off
//automatically serving support files with the
//'supportFile' configuration option.

//You can read more here:
//https://on.cypress.io/configuration
//***********************************************************

//Import commands.js using ES2015 syntax:
import './commands'

//Alternatively you can use CommonJS syntax:
//require('./commands')

beforeEach(function(){
	let uuid = require('uuid');
	let randomRegistryName = Cypress.env('whoami') + uuid.v4();
	cy.wrap(randomRegistryName).as('registryName');
})


afterEach(function(){
	cy.get("@registryName").then(function (registryName) {
		cy.log("removing DynamoDB table " + registryName)

		waitUntilRegistryIsReady(registryName, 0)

		cy.get('@registryAdminApiKey').then(function (apiKey) {

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
		})
	})
})

function waitUntilRegistryIsReady(registryName, count) {
	let statusUrl = '/registry/' + registryName + '/status'
	cy.log('waiting...')
	cy.log('counter: ' + count)

	cy.request({
		url: statusUrl,
		failOnStatusCode: false
	}).then(function (response){
		if(response.status === 303){
			const newCount = count + 1;
			cy.log('newCount: ' + newCount)
			if(newCount < 5){
				cy.wait(5000)
				waitUntilRegistryIsReady(registryName, newCount)
			}
		}

	})
	cy.log('Done waiting...')
}

