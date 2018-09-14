given('that there is an {userType} user', (userType) => {
	
})

when('the {userType} user provides valid credentials', (userType) => {
	expect(userType).to.equal("API admin")
	// check credentials
})

then('the {userType} user is authorised to use the admin API services', (user) => {
	let userAuthorised = true
	cy.wrap(userAuthorised).as("userAuthorised")
})