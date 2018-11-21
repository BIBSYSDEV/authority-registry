/* global defineParameterType, Given, When, Then */

given('that the API admin user has a valid API key for API administration', () =>{
	let apiAdminApiKey = 'testApiAdminApiKey';
	cy.wrap(apiAdminApiKey).as('apiAdminApiKey')
})
