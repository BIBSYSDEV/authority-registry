/* global defineParameterType, Given, When, Then */

given('that the registry admin user has a valid API key for registry administration', () =>{
	cy.log('registry_admin__common.js')
	
	cy.wrap('testApiAdminApiKey').as('apiAdminApiKey')
	cy.wrap('dummy').as('registryAdminApiKey');
})
