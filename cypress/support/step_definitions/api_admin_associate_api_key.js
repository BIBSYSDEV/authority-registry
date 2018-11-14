//  Scenario: An API admin user associates an API key with the registry admin role for a registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user submits a new API key to replace the current valid API key
//    Then the API key is updated


when('the API admin user submits a new API key to replace the current valid API key', () =>{
	cy.get("@registryName").then((registryName) => {
		cy.get("@apiAdminApiKey").then((apiKey) => {
			let addApikeyUrl = "/registry/" + registryName + "/apikey";
			cy.wrap('').as('newApiKey')
			cy.fixture('newApiKeyData.json')
			.then((testData) => {
				cy.request({
					url: addApikeyUrl,
					method: 'POST',
					headers: {
						'apiKey': apiKey
					},
					body: testData
				}).then((response) => {
					// test return from create
					cy.log(response.body)
					cy.wrap('newApiKey').as('newApiKey')
				})
			})
		})
	})
})

then('the API key is updated', () => {
	cy.get('@returnId').then((returnId) => {
		let getEntityUrl = "/registry/" + registryName + "/entity/" + returnId;

		cy.request(getEntityUrl)
	})
})
