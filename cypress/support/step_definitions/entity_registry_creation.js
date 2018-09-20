//  Scenario: An API admin user creates a new entity registry
//    Given that the API admin user is authenticated
//    When the API admin user provides a properly formatted create-entity-registry-request providing information about:
//      | Registry name              |
//      | Registry admin users       |
//      | Registry validation schema |
//    Then an entity registry that accepts only valid data is created

let createEntityRegistryRequest ={
		name: "registry name",
		adminUsers: ["user1", "user2"],
		validationSchema: "schema"
}

function validateSchema(schema){
	// schema validation here
	if(schema){
		return true
	}
	
	return false
}

let createRegistryEndpoint = "http://ada.bibsys.no/admin/ping" // use create registry lambda when ready

when('the {userType} user provides a properly formatted create-entity-registry-request providing information about:', (user, dataTable) =>{
	
//	expect(dataTable.rawTable[0]).to.equal("Registry name")
	expect(user).to.equal("API admin")
	
	expect(createEntityRegistryRequest.name).to.not.be.undefined;
	expect(createEntityRegistryRequest.name).to.be.a('string');
	expect(createEntityRegistryRequest.name).to.have.length.above(0);

	expect(createEntityRegistryRequest.adminUsers).to.not.be.undefined;
	expect(createEntityRegistryRequest.adminUsers).to.be.a('array');
	expect(createEntityRegistryRequest.adminUsers).to.have.length.above(0);
	expect(createEntityRegistryRequest.adminUsers[0]).to.have.length.above(0);

	expect(createEntityRegistryRequest.validationSchema).to.not.be.undefined;
	expect(createEntityRegistryRequest.validationSchema).to.be.a('string');
	expect(createEntityRegistryRequest.validationSchema).to.have.length.above(0);
	
	expect(validateSchema(createEntityRegistryRequest.validationSchema)).to.be.true;
})

then('an entity registry that accepts only valid data is created', () =>{
	 cy.request(createRegistryEndpoint, createEntityRegistryRequest)
	 	.then((response) => {
//	 		expect(response.body).to.have.property('name', createEntityRegistryRequest.name)
	 	})
})