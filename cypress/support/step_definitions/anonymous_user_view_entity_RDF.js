//  Scenario: An anonymous user views an entity specifying an RDF serialization
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    Then anonymous user can view the data in the given serialization

then('anonymous user can view the data in the given serialization', () => {
	cy.log('-- anonymous_user_view_entity_RDF.js --')
	cy.get('@getResponse').then((response) => {
		cy.get('@format').then((format) => {
//			expect(response.headers['content-type']).contains(format)
//			test response body for something?
//			set up multiple tests for all the formats?
		})
	})

})