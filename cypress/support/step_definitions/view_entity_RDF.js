then('anonymous user can view the data in the given serialization', () => {
	cy.get('@getResponse').then((response) => {
		cy.get('@format').then((format) => {
//			expect(response.headers['content-type']).contains(format)
//			test response body for something?
//			set up multiple tests for all the formats?
		})
	})

})