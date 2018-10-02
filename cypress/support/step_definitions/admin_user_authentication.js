//  Scenario: An API admin user authenticates themselves
//    Given that there is an API admin user with valid credentials
//    When they provide these credentials
//    Then they are authenticated and receive a valid authentication token

let credentials = "";
let authenticationUrl = "https://www.unit.no"; // authentication service here
let authenticated = 'not authenticated';

given('that there is an API admin user with valid credentials', () => {
	credentials = "API admin user credentials";
})

when('they provide these credentials', () => {
	cy.request(authenticationUrl, credentials)
		.then((response) => { // check if authenticated
			authenticated = 'authenticated';
			cy.wrap(authenticated).as('authenticated')
		})
})

then('they are authenticated and receive a valid authentication token', () => {
	cy.get('@authenticated').should('equal', 'authenticated')
})