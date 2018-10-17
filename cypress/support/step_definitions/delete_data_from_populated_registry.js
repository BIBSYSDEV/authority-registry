//Scenario: An API admin user deletes populated data from an entity registry
//Given that the API admin user is authenticated
//And that there is an existing, populated entity registry with a schema
//When the API admin user deletes the data in the entity registry
//Then the API admin user receives information that the data is deleted

when('the API admin user deletes the data in the entity registry', () => {
	let deleteDataUrl = '/registry';
	cy.get('@authenticationToken').then((authToken) => {
		cy.get("@registryName").then((registryName) => {
			let deleteRequest = {
					"registryName": registryName,
					"action": "EMPTY"
			};
			cy.request({
				url: deleteDataUrl,
				method: "POST",
				body: deleteRequest,
				headers: {
					Authorization: 'Token ' + authToken
				}
			}).then((response) => {
				cy.log(response.body)
				cy.wrap('Data in registry has been deleted').as('deleteConfirmation')
//				cy.wrap(response.body).as('deleteConfimation')
			})
		})
	})
})

then('the API admin user receives information that the data is deleted', () => {
	cy.get('@deleteConfirmation').then((deleteComfirmation) => {
		expect(deleteComfirmation).to.equals('Data in registry has been deleted')
	})
})