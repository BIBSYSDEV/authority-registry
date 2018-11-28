when('the anonymous user requests the entity specifying an Accept header with value:',(dataTable) => {

	let formats = dataTable.rawTable;
	cy.wrap(formats).as('formats');
	let resultMap = [];

	cy.get('@registryName').then((registryName) => {
		cy.get('@entityId').then((entityId) => {
			let getUrl = 'registry/' + registryName + '/entity/' + entityId
			formats.forEach(format => {
				cy.request({
					url: getUrl,
					headers: {
						'Accept': format
					}
				}).then((response) => {
					resultMap.push({
						format: response
					})
				})
			})
			cy.wrap(resultMap).as('results');
		})
	})
})

//Scenario: An anonymous user views an entity specifying a specific MARC format
//Given that there is an existing entity registry with a schema
//And that there is an entity in the registry
//When the anonymous user requests the entity specifying an Accept header with value:
//| application/marcxml+xml |
//| application/marc        |
//| application/mads+xml    |
//| application/marcxml     |
//Then anonymous user can view the data in the given MARC format

then('anonymous user can view the data in the given MARC format', () => {
	cy.get('@results').then((results) => {
		cy.get('@formats').then((formats) => {
			formats.forEach(format => {
				assert.equals(format, results[format])
			})
		})
	})
})

//Scenario: An anonymous user views an entity specifying a specific RDF serialization and a specific profile
//Given that there is an existing entity registry with a schema
//And that there is an entity in the registry
//When the anonymous user requests the entity specifying an Accept header with value:
//| application/ld+json     |
//| application/n-triples   |
//| application/rdf+xml     |
//| application/turtle      |
//| application/json        |
//| application/rdf         |
//And specifies an Accept-schema header with a value:
//| native-uri   |
//| skos-uri     |
//| bibframe-uri |
//Then anonymous user can view the data in the serialization and profile requested

when('specifies an Accept-schema header with a value:', (dataTable) => {
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

//Scenario: An anonymous user views an entity specifying an RDF serialization
//Given that there is an existing entity registry with a schema
//And that there is an entity in the registry
//When the anonymous user requests the entity specifying an Accept header with value:
//| application/ld+json     |
//| application/n-triples   |
//| application/rdf+xml     |
//| application/turtle      |
//| application/json        |
//| application/rdf         |
//Then anonymous user can view the data in the given serialization

then('anonymous user can view the data in the given serialization', () => {
	cy.get('@getResponse').then((response) => {
		cy.get('@format').then((format) => {
//			expect(response.headers['content-type']).contains(format)
//			test response body for something?
//			set up multiple tests for all the formats?
		})
	})

})
