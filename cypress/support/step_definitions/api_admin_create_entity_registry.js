//  Scenario: An API admin user creates a new entity registry
//    Given that the API admin user has a valid API key for API administration
//    When the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:
//      | Registry name              |
//      | Registry admin users       |
//      | Registry validation schema |
//    Then an entity registry that accepts only valid data is created

let createEntityRegistryRequest ={
		'id': 'name',
		metadata: {
			name: 'name',
			createDate: '2018-01-01',
			registryName: 'registryName',
			label: ['label'],
			license: 'license',
			contributor: ['contributor'],
			creator: ['creator'],
			description: 'description',
			sameAs: ['sameAs']

		}
}

let createRegistryEndpoint = '/registry';

when('the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:', (dataTable) =>{

	let attributeArray = dataTable.rawTable;

	expect(createEntityRegistryRequest['id']).to.be.a('string');
	expect(createEntityRegistryRequest['id']).to.have.length.above(0);

	let schemaValidationUrl = '/registry/schema';
	cy.request(schemaValidationUrl, createEntityRegistryRequest[attributeArray[2]])

})

then('an entity registry that accepts only valid data is created', () =>{

	cy.get("@registryName").then((randomRegistryName) =>{

		createEntityRegistryRequest['id'] = randomRegistryName

		cy.request({ 
			url: createRegistryEndpoint, 
			body: createEntityRegistryRequest,
			method: 'POST',
			headers: {
				'content-type': 'application/json'
			}
		})
		.then((response) => {
			expect(response.body['message']).to.contain(randomRegistryName)
		})
		
		let getRegistryUrl = '/registry/' + randomRegistryName;
		cy.request(getRegistryUrl)
	})
})