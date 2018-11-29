// Scenario: An anonymous user views the metadata for a registry as RDF
//    Given that there is an existing populated entity registry with a schema
//    When an anonymous user dereferences the base URI for the registry specifying mediatypes:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    Then they see metadata related to the entity registry regarding:
//      | Metatata                |
//      | Available data profiles |


when('an anonymous user dereferences the base URI for the registry specifying mediatypes:', (dataTable) =>{
	cy.log('-- anonymous_user_view_entity_registry_metadata_RDF.js --')
	let attributeArray = dataTable.rawTable;

	cy.get('@registryName').then((registryName) => {
		
		let getEntityMetadataUrl = '/registry/' + registryName;
		cy.request({
			url: createRegistryEndpoint, 
			method: 'GET',
			headers: {
				'content-type': 'text/html'
			}
		}).then((response) => {
			cy.wrap(response).as('registryMetadata');
		})
	})

})

then('they see metadata related to the entity registry regarding:', (dataTable) =>{
	let attributeArray = dataTable.rawTable;

	cy.get('@registryMetadata').then((metadata) => {
		
	})
})