//  Scenario: An API admin user attempts to update the validation schema of an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
//    Then the API admin user receives information that they cannot update the entity registry validation schema until the populated data is deleted

when('the API admin user uses the API key and submits a request to update the validation schema of the entity registry', () => {
	cy.get('@registryName').then((registryName) => {
		let updateRegistryUrl = '/registry/' + registryName + '/schema';

		cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
			cy.fixture('registryTestSchemaUpdated').then((updatedSchema) => {
				cy.request({
					url: updateRegistryUrl,
					method: 'PUT',
					headers: {
						'api-key': apiAdminApiKey
					},
					failOnStatusCode: false,
					body: updatedSchema
				}).then((response) => {
					expect(response.status).to.not.equals(200)
					cy.wrap(response.body).as('errorMessage')
				})
			})
		})
	})
})

then('the API admin user receives information that they cannot update the entity registry validation schema until the populated data is deleted',() => {
	cy.get('@errorMessage').then((errorMessage) => {
		expect(errorMessage).to.contains('Error updating registry, registry not empty')
	})

})