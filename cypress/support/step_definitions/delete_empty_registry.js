given('that there is an existing, empty entity registry with a schema', () => {
	// create new empty registry
	let entityRegistryUrl = 'http://ada.bibsys.no/admin/ping';
	cy.fixture("registryTestSchema.json").then((testSchema) => {
		cy.get('@authenticationToken').then((authToken) => {
			cy.request({
				url: entityRegistryUrl,
//				method: 'POST',
				headers: {
					Authorization: 'Token ' + authToken
				},
				body: testSchema
			})
		})
	})
})

when('the API admin user request deletion of an entity registry', () => {
	// delete empty registry
	let deleteRegistryUrl = 'http://ada.bibsys.no/admin/ping';
	cy.request(deleteRegistryUrl)
})

then('the empty entity registry is deleted', () => {
	// call registry
	let registryHeartbeatUrl = 'http://ada.bibsys.no/admin/ping';
	expect(cy.request(registryHeartbeatUrl))
})