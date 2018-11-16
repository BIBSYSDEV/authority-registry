/* global defineParameterType, Given, When, Then */

given('that the API admin user has a valid API key for API administration',(userType) =>{
	let apiAdminApiKey = 'testApiAdminApiKey';
	cy.wrap(apiAdminApiKey).as('apiAdminApiKey')
})
