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

const SERVICE_UNAVAILABLE = 503
const SEE_OTHER = 303


beforeEach(function(){
	let uuid = require('uuid');
	let whoami = Cypress.env('whoami');
	if(whoami === undefined){
		whoami = 'test_'
	}
	
	let randomRegistryName = whoami + uuid.v4();
	cy.wrap(randomRegistryName).as('registryName');
	
	let apiKey = Cypress.env('apiKey');
	if(apiKey === undefined){
		apiKey = 'testApiAdminApiKey'
	}
	
	cy.wrap(true).as('cleanUp') 
	cy.wrap(apiKey).as('apiAdminApiKey')
})


afterEach(function(){
	cy.get('@cleanUp').then((doCleanUp) => {
		if(doCleanUp){
			cy.get("@registryName").then(function (registryName) {
				cy.log("removing DynamoDB table " + registryName)

				cy.registryReady(registryName)

				cy.get('@registryAdminApiKey').then(function (apiKey) {

					cy.deleteRegistry(registryName, apiKey);
				})
			})
		}
	})
})

function waitUntilRegistryIsReady(registryName, count) {
	const statusUrl = '/registry/' + registryName + '/status'
	cy.log('waiting...')
	cy.log('counter: ' + count)

	cy.request({
		url: statusUrl,
		failOnStatusCode: false
	}).then(function (response){
		if(response.status === SEE_OTHER||response.status === SERVICE_UNAVAILABLE){
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

