given('that there is an existing, populated entity registry with a schema', () => {
	// delete any existing registry here
	cy.fixture('registryTestSchema').then((testSchema) => {
		cy.get('@authenticationToken').then((authToken) => {
			let entityRegistryUrl = 'http://ada.bibsys.no/admin/ping'
				cy.request({
					url: entityRegistryUrl,
					headers: {
						Authorization: 'Token ' + authToken
					},
					body: testSchema
				})
		})
	})

	cy.fixture('entityTestData').then((testData) => {
		cy.get('@authenticationToken').then((authToken) => {
			let createEntityUrl = 'http://ada.bibsys.no/admin/ping';
			cy.request({
				url: createEntityUrl,
				headers: {
					Authorization: 'Token ' + authToken
				},
				body: testData
			})
		})
	})
})
