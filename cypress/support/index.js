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
})


afterEach(function(){
	cy.get("@registryName").then(function (registryName) {
		cy.log("removing DynamoDB table " + registryName)

		waitUntilRegistryIsReady(registryName)
		
		cy.get('@registryAdminApiKey').then(function (apiKey) {

			cy.log('api-key = ' + apiKey)
			let url = '/registry/' + registryName
			cy.request({
				url: url,
				method: 'DELETE',
				headers: {
					'api-key': apiKey,
					'content-type': 'application/json'
				},
				failOnStatusCode: false
			}).then(function (response) {})
		})
	})
})

function waitUntilRegistryIsReady(registryName) {
	let statusUrl = '/registry/' + registryName + '/status'
	let count = 0;
	cy.log('waiting...')
	let ready = false
	cy.wrap(ready).as('isReady')
	
	function isReady(response){
		var that = this
		if(response.status === 200){
			cy.wrap(true).as('isReady')
			cy.log('status = ' + response.status)
		}
	}
	
	do{ 
		cy.log('ready = ' + that.erter)
		cy.request({
			url: statusUrl,
			failOnStatusCode: false
		}).then(isReady)
//				function (response) {
//			
//			if(response.status === 200){
//				cy.wrap('true').as('ready')
//				cy.log('status = ' + response.status + ' ready = ' + ready)
//				ready = true
//			}
//		})
		
		cy.get('@isReady').then((isReady) => {
			cy.log(isReady)
			ready = isReady
		})
		
		if(!ready){
			cy.wait(5000);
		}
		
		if(count++ >= 3){
			ready = true;
		}
		cy.log('ready = ' + ready + ', count = ' + count)
	}while(!ready);
	cy.log('Done waiting...')
}
