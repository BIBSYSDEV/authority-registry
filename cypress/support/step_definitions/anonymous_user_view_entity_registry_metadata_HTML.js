//  Scenario: An anonymous user views the metadata for a registry as HTML
//    Given that there is an existing populated entity registry with a schema
//    When an anonymous user dereferences the base URI for the registry specifying mediatype text/html
//    Then they see metadata related to the entity registry regarding:
//      | Registry name                    |
//      | Registry type                    |
//      | Publisher                        |
//      | License for the data             |
//      | Owner organisation               |
//      | Participating organisations      |
//      | Languages used in dataset        |
//      | Creation date                    |
//      | Modification date                |
//      | Relations to other data sets     |
//      | Location of APIs                 |
//      | Example resources                |
//      | Base URI for dataset             |
//      | Location of SPARQL endpoint      |
//      | Description of available formats |


when(/an anonymous user dereferences the base URI for the registry specifying mediatype text\/html/, (tableData) =>{
	cy.log('-- anonymous_user_view_entity_registry_metadata_HTML.js --')
	
	let attributeArray = dataTable.rawTable;
	cy.wrap(attributeArray).as('attributeNames')
	
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
		cy.get('attributeNames').then((attributeNames) => {
			attributeNames.forEach(attribute => {
				assert.notNull(metadata[attribute])
			})
		})
	})
})
