//  Scenario: A registry admin adds registry admin API keys to an existing, populated entity registry
//    Given that the registry admin user has a valid API key for registry administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user adds registry admin API keys to the entity registry
//    Then the users with the API keys can access the entity registry


when('the API admin user adds registry admin API keys to the entity registry', () =>{
	cy.log('-- registry_admin_add_apikey.js --')
	cy.get("@registryName").then((registryName) => {
		let addApikeyUrl = "/registry/" + registryName + "/apikey";

		cy.wrap('').as('newApiKey')
		cy.fixture('newApiKeyData.json')
		.then((testData) => {
			cy.get('@registryAdminApiKey').then((apiKey) => {
				cy.request({
					url: addApikeyUrl,
					method: 'POST',
					headers: {
						'api-Key': apiKey
					},
					body: testData
				}).then((response) => {
					// test return from create
					cy.log(response.body)
					cy.wrap('return.id').as('returnId')
				})
			})
		})
	})
})

then('the users with the API keys can access the entity registry', () => {
	cy.get('@returnId').then((returnId) => {
		let getEntityUrl = "/registry/" + registryName + "/entity/" + returnId;

		cy.request(getEntityUrl)
	})
})
