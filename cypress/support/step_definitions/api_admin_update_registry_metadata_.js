//  Scenario: An API admin user updates the entity registry metadata
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user changes the metadata for the entity registry
//    Then the metadata for the entity registry is updated

when('the API admin user changes the metadata for the entity registry', () => {
	cy.get('@registryName').then((registryName) => {

		let registryUpdateUrl = '/registry/' + registryName;
		cy.get('@registryAdminApiKey').then((apiKey) => {
			let updatedDescription = 'Updated description';
			cy.wrap(updatedDescription).as('updatedDescription')
			cy.fixture('registryTestSchemaUpdated.json').then((updatedSchema) => {
				updatedSchema.metadata.description = updatedDescription
				cy.request({
					url: registryUpdateUrl,
					method: 'PUT',
					headers: {
						'api-key': apiKey,
					},
					body: updatedSchema
				})
			})
		})
	})
})

then('the metadata for the entity registry is updated', () => {
	cy.get('@registryName').then((registryName) => {
		let registryGetUrl = '/registry/' + registryName;
		cy.get('@registryAdminApiKey').then((apiKey) => {
			cy.request({
				url: registryGetUrl,
				method: 'GET',
				headers: {
					'api-key': apiKey
				},
				body: updatedSchema
			}).then((response) => {
				cy.get('@updatedDescription').then((updatedDescription) => {
					expect(response.body.metadata.description).to.equals(updatedDescription)
				})
			})
		})
	})
})