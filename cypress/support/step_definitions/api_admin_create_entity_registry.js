//Scenario: An API admin user creates a new entity registry
//Given that the API admin user has a valid API key for API administration
//When the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:
//| Registry name              |
//| Registry admin users       |
//| Registry validation schema |
//Then an entity registry that accepts only valid data is created

let createRegistryEndpoint = '/registry';

when('the API admin user submits the API key and a properly formatted create-entity-registry-request providing information about:', (dataTable) =>{

	let attributeArray = dataTable.rawTable;
	cy.fixture('registryTestMetadata.json').then((createEntityRegistryRequest) => {

		createEntityRegistryRequest.id = registryName;

		cy.get('@registryName').then((registryName) => {
			cy.get('@apiAdminApiKey').then((apiKey) => {
				let url = '/registry';
				cy.request({
					url: url,
					method: 'POST',
					body: createEntityRegistryRequest,
					headers: {
						'api-key': apiKey,
						'content-type': 'application/json'
					}
				}).then((response) => {})

			})
		})
	})
})

then('an entity registry that accepts only valid data is created', () =>{

	cy.get("@registryName").then((registryName) =>{

		let getRegistryUrl = '/registry/' + registryName;
		cy.request(getRegistryUrl).then((response) => {

		})
	})
})