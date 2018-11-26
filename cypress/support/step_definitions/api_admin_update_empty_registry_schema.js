//  Scenario: An API admin user updates an existing, empty entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
//    Then the entity registry is updated

when('the API admin user uses the API key and submits a request to update the validation schema of the entity registry', () => {
	cy.get('@registryName').then((registryName) => {

		let registryUpdateUrl = '/registry/' + registryName + '/schema';
		cy.get('@registryApiKey').then((registryApiKey) => {
			cy.fixture('registryTestSchemaUpdated.json')
			.then((updatedSchema) => {
				updatedSchema['metadata'].description = updatedDescription
				cy.request({
					url: registryUpdateUrl,
					method: 'PUT',
					headers: {
						'api-key': registryApiKey,
					},
					body: updatedSchema
				})
			})
		})
	})
})

then('the entity registry is updated', () => {
	cy.get('@registryName').then((registryName) => {
		let registryGetUrl = '/registry/' + registryName + '/schema';
		cy.get('@authenticationToken').then((authToken) => {
			cy.request({
				url: registryGetUrl,
				method: 'GET',
				headers: {
					Authorization: 'Token ' + authToken,
				},
				body: updatedSchema
			}).then((response) => {
				cy.get('@updatedDescription').then((updatedDescription) => {
					expect(response.body['metadata'].description).to.equals(updatedDescription)
				})
			})
		})
	})
})