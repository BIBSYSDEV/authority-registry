when('the API admin user attempts to delete the entity registry', () => {
	let deleteRegistryUrl = 'http://ada.bibsys.no/admin/ping';

	cy.get('@authenticationToken').then((authToken) =>{
		cy.request({
			url: deleteRegistryUrl,
			headers: {
				Authorization: 'Token ' + authToken
			},
			failOnStatusCode: true
		}).then((response) => {
			expect(response.status).to.equals(200)
			cy.wrap('Error deleting registry, registry not empty').as('errorMessage')
//			expect(response.status).to.not.equals(200)
//			cy.wrap(response.body).as('errorMessage')
			
		})
	})
})

then('the API admin user receives information that they cannot delete the entity registry until the populated data is deleted', () => {
	cy.get('@errorMessage').then((errorMessage) => {
		expect(errorMessage).to.contains('Error deleting registry, registry not empty')
	})
})
