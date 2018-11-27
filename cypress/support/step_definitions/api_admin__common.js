/* global defineParameterType, Given, When, Then */

given('that the API admin user has a valid API key for API administration', () =>{
	cy.log('api_admin__common.js')
	
	cy.wrap('testApiAdminApiKey').as('apiAdminApiKey')
	cy.wrap('dummy').as('registryAdminApiKey');
})
