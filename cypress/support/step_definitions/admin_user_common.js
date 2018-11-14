/* global defineParameterType, Given, When, Then */

defineParameterType({
	name: "userType",
	regexp: "API admin|registry admin|anonymous"
	})

given('that the API admin user has a valid API key for API administration',(userType) =>{
	let authenticationToken = 'fac116dd-1b6d-4c95-85ed-b90dd51a480e';
	cy.wrap(authenticationToken).as('authenticationToken')
})
