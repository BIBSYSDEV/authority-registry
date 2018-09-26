//  Scenario: An anonymous user views an entity specifying a specific RDF serialization and a specific profile
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    And specifies an Accept-schema header with a value:
//      | native-uri   |
//      | skos-uri     |
//      | bibframe-uri |
//    Then anonymous user can view the data in the serialization and profile requested

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