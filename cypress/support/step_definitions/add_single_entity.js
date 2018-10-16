//Scenario: An registry admin user adds a single entity to a registry
//Given that the registry admin user is authenticated
//And that there is an existing entity registry with a schema
//When the registry admin user requests the creation of a new entity with properly formatted data
//Then the entity is created


when('the registry admin user requests the creation of a new entity with properly formatted data', () =>{
	cy.get("@registryName").then((registryName) => {
		let createEntityUrl = "/registry/" + registryName;

		cy.wrap('').as('returnId')
		cy.fixture('entityTestData.json')
		.then((testData) => {
			cy.get('@authenticationToken').then((authToken) => {
				cy.request({
					url: createEntityUrl,
					method: 'POST',
					headers: {
						Authorization: 'Token ' + authToken
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

then('the entity is created', () => {
	cy.get('@returnId').then((returnId) => {
//		let getEntityUrl = "/registry/" + registryName + "/" + returnId;
		let getEntityUrl = "https://www.unit.no";

		cy.request(getEntityUrl)
	})
})
