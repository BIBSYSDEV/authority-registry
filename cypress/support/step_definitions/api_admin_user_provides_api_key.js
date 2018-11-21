//  Scenario: An API admin user provides a valid API key
//    Given that an API admin user has a valid API key for API administration
//    When they submit the API key
//    Then they can access the administration APIs

given('that the registry admin user has a valid API key for registry administration', () => {
	
	cy.wrap('testApiAdminApiKey').as('apiAdminApiKey');
})

when('they submit the API key', () => {
	cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
		cy.get('@registryName').then((registryName) => {
			// create new test registry metadata
			cy.fixture('registryTestMetadata.json')
			.then((testSchema) => {
				testSchema.registryName = registryName;
				let createUrl = '/registry';
				cy.request({
					url: createUrl,
					method: 'POST',
					body: testSchema, 
					headers: {
						'x-api-key': apiAdminApiKey
					}
				}).then((response) => {
					cy.wrap(response.status).as('responseStatus')
				})
			})
		})
	})
})

then('they can access the administration APIs', () => {
	cy.get('@responseStatus').then((status) => {
		assert.notEquals(status, 403)
	})
})
