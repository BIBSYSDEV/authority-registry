when('an anonymous user requests the OpenAPI documentation', () => {
	let openApiDocumentationUrl = 'http://ada.bibsys.no/admin/ping';
	cy.request(openApiDocumentationUrl).then((response) => {
		cy.wrap(response.body).as('documentation')
	})
		
})

then('the OpenAPI documentation is returned', () =>  {
	cy.get('@documentation').then((documentation) => {
//		expect(documentation).to.contain('test registry')
	})
})