//  Scenario: An API admin user attempts to delete an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user uses the API key and submits a request to delete the entity registry
//    Then the API admin user receives information that they cannot delete the entity registry until the populated data is deleted

when('the API admin user uses the API key and submits a request to delete the entity registry', () => {

	cy.get("@registryName").then((registryName) => {

		let deleteRegistryUrl = '/registry/' + registryName;

		cy.get('@apiAdminApiKey').then((apiKey) =>{
			cy.request({
				url: deleteRegistryUrl,
				method: 'DELETE',
				headers: {
					'apikey': apiKey
				},
				failOnStatusCode: true
			}).then((response) => {
				expect(response.status).to.equals(403)
				cy.wrap('Error deleting registry, registry not empty').as('errorMessage')
			})
		})
	})
})

then('the API admin user receives information that they cannot delete the entity registry until the populated data is deleted', () => {
	cy.get('@errorMessage').then((errorMessage) => {
		expect(errorMessage).to.contains('Error deleting registry, registry not empty')
	})
})