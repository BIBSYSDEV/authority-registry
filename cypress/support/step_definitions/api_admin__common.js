/* global defineParameterType, Given, When, Then */

given('that the API admin user has a valid API key for API administration',(userType) =>{
	let apiAdminApiKey = 'fac116dd-1b6d-4c95-85ed-b90dd51a480e';
	cy.wrap(apiAdminApiKey).as('apiAdminApiKey')
})
