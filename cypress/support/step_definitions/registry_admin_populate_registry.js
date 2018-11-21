//  Scenario: A registry admin user populates a registry
//    Given that the registry admin user has a valid API key for registry administration
//    And that there is an existing entity registry with a schema
//    And that the registry admin user has a set of properly schema-formatted data
//    When the registry admin user submits an API key and a request to bulk upload the data to the entity registry
//    Then the data is available in the entity registry

given('that the registry admin user has a set of properly schema-formatted data', () => {
	cy.fixture('testDataBulk.json').as('bulkUpload')
	// test against schema here?
})

when('the registry admin user submits an API key and a request to bulk upload the data to the entity registry', () => {
	let bulkUploadUrl = 'https://www.unit.no';
	cy.get('@bulkUpload').then((bulkUpload) => {
		cy.get('@registryAdminApiKey').then((apiKey) => {
			cy.request({
				url: bulkUploadUrl,
				header: {
					'x-api-key': apiKey
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
			expect(Object.keys(response.body).length).to.equals(10)
		})
	})
})