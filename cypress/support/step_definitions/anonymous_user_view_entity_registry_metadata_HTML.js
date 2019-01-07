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
	
	cy.get('@registryName').then((registryName) => {
		
		const registryEndpoint = '/registry/' + registryName;
		cy.request({
			url: registryEndpoint, 
			method: 'GET',
			headers: {
				'Accept': 'text/html'
			}
		}).then((response) => {
			cy.server()
			cy.route('GET', registryEndpoint, response)
		})
	})

})

then('they see metadata related to the entity registry regarding:', (dataTable) =>{
	const attributeArray = dataTable.rawTable;

	cy.get('@registryName').then((registryName) => {
		cy.visit('/registry/' + registryName)
		
		cy.contains(registryName)
		cy.contains('descriptionValue')
	})
})
