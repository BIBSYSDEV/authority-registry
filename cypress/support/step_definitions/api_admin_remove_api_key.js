//  Scenario: An API admin user removes registry admin API keys from an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema and registered registry API keys
//    When the API admin user removes registry admin API keys from the entity registry
//    Then the API keys no longer provide access to the entity registry


when('the API admin user removes registry admin API keys from the entity registry', () =>{
	cy.get("@registryName").then((registryName) => {
		cy.get("@apiAdminApiKey").then((apiKey) => {
			let addApikeyUrl = "/registry/" + registryName + "/apikey";
			cy.fixture('newApiKeyData.json')
			.then((testData) => {
				cy.request({
					url: addApikeyUrl,
					method: 'DELETE',
					headers: {
						'apiKey': apiKey
					},
					body: testData
				}).then((response) => {
					// test return from create
					cy.log(response.body)
				})
			})
		})
	})
})

then('the API keys no longer provide access to the entity registry', () => {
	cy.get('@returnId').then((returnId) => {
		let getEntityUrl = "/registry/" + registryName + "/entity/" + returnId;

		cy.request(getEntityUrl)
	})
})
