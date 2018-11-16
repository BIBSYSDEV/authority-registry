//  Scenario: An registry admin user adds a single entity to a registry
//    Given that the registry admin user has a valid API key for registry administration
//    And that there is an existing entity registry with a schema
//    When the registry admin user submits the API key with a request to create a new entity with properly formatted data
//    Then the entity is created


when('the registry admin user submits the API key with a request to create a new entity with properly formatted data', () =>{
	cy.get("@registryName").then((registryName) => {
		let createEntityUrl = "/registry/" + registryName + "/entity";

		cy.wrap('').as('returnId')
		cy.fixture('entityTestData.json')
		.then((testData) => {
			cy.get('@registryAdminApiKey').then((apiKey) => {
				cy.request({
					url: createEntityUrl,
					method: 'POST',
					headers: {
						'api-key': apiKey
					},
					body: testData
				}).then((response) => {
					// test return from create
					cy.wrap(response.body).as('returnUri')
				})	
			})
		})
	})
})

then('the entity is created', () => {
	cy.get('@returnUri').then((returnUri) => {
		cy.request(returnUri);
	})
})
