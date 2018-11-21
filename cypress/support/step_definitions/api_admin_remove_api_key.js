//  Scenario: An API admin user removes registry admin API keys from an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema and registered registry API keys
//    When the API admin user removes registry admin API keys from the entity registry
//    Then the API keys no longer provide access to the entity registry

function createTestEntity(apikey, expectedStatus) {
	cy.fixture('entityTestData.json')
	.then((testData) => {
		let createEntityUrl = "/registry/" + registryName + '/entity';
		cy.request({
			url: createEntityUrl,
			method: 'POST',
			failOnStatusCode: false,
			headers: {
				'x-api-key': apiKey
			}
		}).then((response) => {
			assert.equals(response.status, expectedStatus)
		})
	})

}

when('the API admin user removes registry admin API keys from the entity registry', () =>{
	cy.get("@registryName").then((registryName) => {
		cy.get("@apiAdminApiKey").then((apiAdminApiKey) => {
			let addApikeyUrl = "/registry/" + registryName + "/apikey";
			cy.fixture('newApiKeyData.json')
			.then((testData) => {
				cy.wrap(testData.apikey).as('newApiKey');
				cy.request({
					url: addApikeyUrl,
					method: 'POST',
					headers: {
						'x-api-key': apiAdminApiKey
					},
					body: testData
				})
				cy.get('@newApiKey').then((newApiKey) => {
					createTestEntity(newApiKey, 200); // test that there is a valid apikey that provide access to the entity registry
				})
				
				let deleteApiKeyUrl = "/registry/" + registryName + "/apikey/" + 
				cy.request({
					url: deleteApiKeyUrl,
					method: 'DELETE',
					headers: {
						'x-api-key': apiAdminApiKey
					}
				})
			})
		})
	})
})

then('the API keys no longer provide access to the entity registry', () => {
	cy.get('@returnId').then((returnId) => {
		cy.get('@newApiKey').then((newApiKey) => {
			createTestEntity(newApiKey, 403);
		})
	})
})
