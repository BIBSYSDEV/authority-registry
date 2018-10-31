//Scenario: An API admin user creates a new entity registry
//Given that the API admin user is authenticated
//When the API admin user provides a properly formatted create-entity-registry-request providing information about:
//| Registry name              |
//| Registry admin users       |
//| Registry validation schema |
//Then an entity registry that accepts only valid data is created

let createEntityRegistryRequest ={
		'name': 'test1'
//			'registryAdminUsers': ['user1', 'user2'],
//			'registryValidationSchema': 'schema'
}

let createRegistryEndpoint = '/registry'

	when('the API admin user provides a properly formatted create-entity-registry-request providing information about:', (dataTable) =>{

		let attributeArray = dataTable.rawTable;

		expect(createEntityRegistryRequest['name']).to.be.a('string');
		expect(createEntityRegistryRequest['name']).to.have.length.above(0);

//		expect(createEntityRegistryRequest['registryAdminUsers']).to.be.a('array');
//		expect(createEntityRegistryRequest['registryAdminUsers']).to.have.length.above(0);
//		expect(createEntityRegistryRequest['registryAdminUsers'][0]).to.be.a('string');
//		expect(createEntityRegistryRequest['registryAdminUsers'][0]).to.have.length.above(0);

//		expect(createEntityRegistryRequest['registryValidationSchema']).to.be.a('string');
//		expect(createEntityRegistryRequest['registryValidationSchema']).to.have.length.above(0);

		let schemaValidationUrl = 'https://www.unit.no';
		cy.request(schemaValidationUrl, createEntityRegistryRequest[attributeArray[2]])
		.then((response) => {
			expect(true).to.be.true;
		})

	})

	then('an entity registry that accepts only valid data is created', () =>{

		cy.get("@registryName").then((randomRegistryName) =>{

			createEntityRegistryRequest['name'] = randomRegistryName

			cy.request({ 
				url: createRegistryEndpoint + "/" + randomRegistryName, 
				body: createEntityRegistryRequest,
				method: 'PUT',
				headers: {
					'content-type': 'application/json'
				}
			})
			.then((response) => {
				expect(response.body['message']).to.contain(randomRegistryName)
			})
		})
	})