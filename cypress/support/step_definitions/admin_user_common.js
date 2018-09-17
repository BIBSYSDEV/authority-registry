/* global defineParameterType, Given, When, Then */

defineParameterType({
	name: "userType",
	regexp: "API admin|registry admin|anonymous"
	})

given('that the {userType} user is authenticated',(userType) =>{
	let authenticationToken = "authenticated";
	cy.wrap(authenticationToken).as("authenticationToken")
})
