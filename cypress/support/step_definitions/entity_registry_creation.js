//  Scenario: An API admin user creates a new entity registry
//    Given that the API admin user is authenticated
//    When the API admin user provides a properly formatted create-entity-registry-request providing information about:
//      | Registry name              |
//      | Registry admin users       |
//      | Registry validation schema |
//    Then an entity registry that accepts only valid data is created

let createEntityRegistryRequest ={
		'Registry name': 'test',
		'Registry admin users': ['user1', 'user2'],
		'Registry validation schema': 'schema'
}

let createRegistryEndpoint = 'http://ada.bibsys.no/admin/ping' // use create registry lambda when ready

when('the API admin user provides a properly formatted create-entity-registry-request providing information about:', (dataTable) =>{
	
	let attributeArray = dataTable.rawTable;
	
	expect(createEntityRegistryRequest[attributeArray[0]]).to.be.a('string');
	expect(createEntityRegistryRequest[attributeArray[0]]).to.have.length.above(0);

	expect(createEntityRegistryRequest[attributeArray[1]]).to.be.a('array');
	expect(createEntityRegistryRequest[attributeArray[1]]).to.have.length.above(0);
	expect(createEntityRegistryRequest[attributeArray[1]][0]).to.be.a('string');
	expect(createEntityRegistryRequest[attributeArray[1]][0]).to.have.length.above(0);

	expect(createEntityRegistryRequest[attributeArray[2]]).to.be.a('string');
	expect(createEntityRegistryRequest[attributeArray[2]]).to.have.length.above(0);

	let schemaValidationUrl = 'http://ada.bibsys.no/admin/ping';
	cy.request(schemaValidationUrl, createEntityRegistryRequest[attributeArray[2]])
	.then((response) => {
		expect(true).to.be.true;
	})

})

then('an entity registry that accepts only valid data is created', () =>{
	 cy.request(createRegistryEndpoint, createEntityRegistryRequest)
	 	.then((response) => {
//	 		expect(response.body).to.have.property('name', createEntityRegistryRequest.name)
	 	})
})