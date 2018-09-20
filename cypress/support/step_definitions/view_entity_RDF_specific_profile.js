given('specifies an Accept-schema header with a value:', (dataTable) => {
	let profileArray = dataTable.rawTable;
	cy.wrap(profileArray[0]).as('profile')
})

then('anonymous user can view the data in the serialization and profile requested', () => {
	cy.get('@entityGetUrl').then((entityGetUrl) => {
		cy.get('@entityId').then((entityId) => {
			cy.request(entityGetUrl)
//			cy.request(entityGetUrl + entityId)
			.then((response) => {
				cy.get('@profile').then((profile) => {
					expect('native-uri').to.contains(profile)
//					expect(response.headers['content-type']).to.contains(profile)
				})
			})
		})
	})
})