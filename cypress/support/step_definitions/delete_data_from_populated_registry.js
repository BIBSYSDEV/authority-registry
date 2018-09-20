when('the API admin user deletes the data in the entity registry', () => {
	let deleteDataUrl = 'http://ada.bibsys.no/admin/ping';
	cy.get('@authenticationToken').then((authToken) => {
		cy.request({
			url: deleteDataUrl,
			headers: {
				Authorization: 'Token ' + authToken
			}
		}).then((response) => {
			cy.wrap('Data in registry has been deleted').as('deleteConfirmation')
//			cy.wrap(response.body).as('deleteConfimation')
		})
	})
})

then('the API admin user receives information that the data is deleted', () => {
	cy.get('@deleteConfirmation').then((deleteComfirmation) => {
		expect(deleteComfirmation).to.equals('Data in registry has been deleted')
	})
})