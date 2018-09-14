let url = "http://ada.bibsys.no/admin/ping"
let authorised = 'not authorised'
	
given('that an API admin user has a valid authentication token', () => {
	let authenticated = 'authenticated'
	cy.wrap(authenticated).as('authenticationToken')
})

when('they submit the authentication token', () => {
	cy.request({
		url: url,
		headers: {
			AuthToken: cy.get('@authenticationToken')
		}
	})
	cy.request(url)
		.then((response) => {
			authorised = 'authorised'
			cy.wrap(authorised).as('authorised')
		})
})

then('they are authorised to access the administration APIs', () => {
	cy.get('@authorised').should('equal', 'authorised')
})