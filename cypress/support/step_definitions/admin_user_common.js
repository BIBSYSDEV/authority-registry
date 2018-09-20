/* global defineParameterType, Given, When, Then */

defineParameterType({
	name: "userType",
	regexp: "API admin|registry admin|anonymous"
	})

given('that the {userType} user is authenticated',(userType) =>{
	let authenticationToken = userType.replace(' ', '_') + '_token';
	cy.wrap(authenticationToken).as('authenticationToken')
})
