//Scenario: An API admin user updates an existing, empty entity registry
//Given that the API admin user is authenticated
//And that there is an existing, empty entity registry with a schema
//When the API admin user updates the metadata and validation schemas of the entity registry
//Then the entity registry is updated

when('the API admin user updates the metadata and validation schemas of the entity registry', () => {
	cy.get('@registryName').then((registryName) => {

		let registryUpdateUrl = '/registry/' + registryName;
		cy.get('@authenticationToken').then((authToken) => {
			let updatedDescription = 'Updated description';
			cy.wrap(updatedDescription).as('updatedDescription')
			cy.fixture('registryTestSchemaUpdated.json').then((updatedSchema) => {
				updatedSchema['metadata'].description = updatedDescription
				cy.request({
					url: registryUpdateUrl,
					method: 'PUT',
					headers: {
						Authorization: 'Token ' + authToken,
					},
					body: updatedSchema
				})
			})
		})
	})
})

then('the entity registry is updated', () => {
	cy.get('@registryName').then((registryName) => {
		let registryGetUrl = '/registry/' + registryName;
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