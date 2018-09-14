/* global defineParameterType, Given, When, Then */

defineParameterType({
	name: "userType",
	regexp: "API admin|registry admin|anonymous"
	})

given('that there is an {userType} user',(user) =>{
	let userName = user + " user";
	cy.wrap(userName).as("userName")
})


given('that the {userType} user is authenticated',(userType) =>{
	let userAuthentication = true;
	cy.wrap(userAuthentication).as("userAuthentication")
})
