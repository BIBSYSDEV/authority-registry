given('that the registry admin user has a set of properly schema-formatted data', () => {
	cy.fixture('testDataBulk.json').as('bulkUpload')
	// test against schema here?
})

when('the registry admin user bulk uploads the data to the entity registry', () => {
	let bulkUploadUrl = 'http://ada.bibsys.no/admin/ping';
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
	let countEntitiesUrl = 'http://ada.bibsys.no/admin/institutions';
	cy.request(countEntitiesUrl).then((response) => {
		expect(Object.keys(response.body).length).to.equals(121)
	})
})