//  Scenario: An API admin user updates an existing, empty entity registry
//    Given that the API admin user is authenticated
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user updates the metadata and validation schemas of the entity registry
//    Then the entity registry is updated

when('the API admin user updates the metadata and validation schemas of the entity registry', () => {
	let registryUpdateUrl = 'http://ada.bibsys.no/admin/ping';
	cy.get('@authenticationToken').then((authToken) => {
		cy.fixture('registryTestSchemaUpdated.json').then((updatedSchema) => {
			cy.request({
				url: registryUpdateUrl,
				headers: {
					Authorization: 'Token ' + authToken,
				},
				body: updatedSchema
			})
		})
	})
})

then('the entity registry is updated', () => {
	// check that the registry schema is updated with correct values
})