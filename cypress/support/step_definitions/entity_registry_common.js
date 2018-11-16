/* global given */

given('that there is an existing entity registry with a schema', () => {
	createEmptyRegistry();
});

given('that there is an entity in the registry', () => {
	createTestEntity()
})

given('that there is an existing entity in the registry', () => {
	createTestEntity()
})

given('that there is an existing, empty entity registry with a schema', () => {
	createEmptyRegistry();
})

given('that there is an existing, populated entity registry with a schema', () => {
	createEmptyRegistry();
	createTestEntity();
})

given('that there is an existing, populated entity registry with a schema and registered registry API keys', () => {
	createEmptyRegistry();
	createTestEntity();
})

function createEmptyRegistry(){
	cy.get('@registryName').then((registryName) => {
		cy.get('@apiAdminApiKey').then((apiAdminApiKey) => {
			// create new test registry metadata
			cy.fixture('registryTestMetadata.json')
			.then((testSchema) => {
				testSchema.registryName = registryName;
				let createUrl = '/registry';
				cy.request({
					url: createUrl,
					method: 'POST',
					body: testSchema, 
					headers: {
						'api-key': apiAdminApiKey
					}
				})

				// add schema to registry
				cy.fixture('registryTestSchema.json')
				.then((testSchema) => {
					let addSchemaUrl = 'registry/' + registryName + '/schema';
					cy.request({
						url: addSchemaUrl,
						method: 'POST',
						body: testSchema, 
						headers: {
							'api-key': apiAdminApiKey
						}
					})
				})
			})
		})
	})
}

function createTestEntity(){

	cy.get('@registryName').then((registryName) => {
		cy.get('@apiAdminApiKey').then((apiKey) => {
			let entityAddUrl = '/registry/' + registryName + '/entity';
			cy.fixture('entityTestData.json') // add testData to registry
			.then((testData) => {
				cy.request({
					url: entityAddUrl,
					method: 'POST',
					body: testData,
					headers: {
						'api-key': apiKey,
						'content-type': 'application/json'
					}
				}).then((response) => {
					let entityUri = response.body
					let entityId = entityUri.split('/')[entityUri.length - 1]
					cy.wrap(entityId).as('entityId');
				})
			})
		})
	})
}

