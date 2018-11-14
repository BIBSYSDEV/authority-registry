//Scenario: A registry admin user populates a registry
//Given that the registry admin user is authenticated
//And that there is an existing entity registry with a schema
//And that the registry admin user has a set of properly schema-formatted data
//When the registry admin user bulk uploads the data to the entity registry
//Then the data is available in the entity registry

given('that the registry admin user has a set of properly schema-formatted data', () => {
	cy.fixture('testDataBulk.json').as('bulkUpload')
	// test against schema here?
})

when('the registry admin user bulk uploads the data to the entity registry', () => {
	let bulkUploadUrl = 'https://www.unit.no';
	cy.get('@bulkUpload').then((bulkUpload) => {
		cy.get('@authenticationToken').then((authToken) => {
			cy.request({
				url: bulkUploadUrl,
				header: {
					Authorization: 'Token ' + authToken
				},
				body: bulkUpload
			})
		})
	})
})

then('the data is available in the entity registry', () => {
	// count number of entities
	cy.get('@registryName').then((registryName) => {

		let countEntitiesUrl = '/registry/' + registryName + '/entity';
		cy.request(countEntitiesUrl).then((response) => {
//			expect(Object.keys(response.body).length).to.equals(121)
		})
	})
})