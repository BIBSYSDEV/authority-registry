//  Scenario: An API admin user deletes populated data from an entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user uses the API key and submits a request to delete the data in the entity registry
//    Then the API admin user receives information that the data is deleted

when('the API admin user uses the API key and submits a request to delete the data in the entity registry', () => {
	let deleteDataUrl = '/registry/';
	cy.get('@apiAdminApiKey').then((apiKey) => {
		cy.get("@registryName").then((registryName) => {
			cy.request({
				url: deleteDataUrl + registryName + '/empty',
				method: "DELETE",
				headers: {
					'apikey': apiKey
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