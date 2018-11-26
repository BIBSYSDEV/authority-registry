/* global given */

given('that there is an existing entity registry with a schema', () => {
	cy.log('creating empty registry')
	createEmptyRegistry(false);
});

given('that there is an existing, empty entity registry with a schema', () => {
	cy.log('creating empty registry')
	createEmptyRegistry(false);
})

given('that there is an entity in the registry', () => {
	createTestEntity()
})

given('that there is an existing entity in the registry', () => {
	createTestEntity()
})

given('that there is an existing, populated entity registry with a schema', () => {
	createEmptyRegistry(true);
	createTestEntity();
})

given('that there is an existing, populated entity registry with a schema and registered registry API keys', () => {
	createEmptyRegistry(true);
	createTestEntity();
})

function createEmptyRegistry(createEntity){
	cy.get('@registryName').then(function (registryName) {
		cy.log('create Entity? ' + createEntity)
		cy.log('Creating schema with name ' + registryName)
		cy.get('@apiAdminApiKey').then(function (apiAdminApiKey) {
			// create new test registry metadata
			cy.log('Using apiKey ' + apiAdminApiKey)
			cy.fixture('registryTestMetadata.json')
			.then(function (testSchema) {
				testSchema.id = registryName;
				let createUrl = '/registry';
				cy.request({
					url: createUrl,
					method: 'POST',
					body: testSchema, 
					headers: {
						'api-key': apiAdminApiKey,
						"content-type": "application/json"
					}
				}).then((response) => {
					cy.wrap(response.body.apiKey).as('registryAdminApiKey')

					if(createEntity){
						cy.log('creating test entity')
						createTestEntity()
					}
				})
			})
		})
	})
}

function createTestEntity(){
	var ready = false
	cy.get('@registryName').then(function (registryName) {
		cy.get('@registryAdminApiKey').then(function (apiKey) {

			waitUntilRegistryIsCreated(registryName, 0);

			let entityAddUrl = '/registry/' + registryName + '/entity';
			cy.fixture('entityTestData.json') // add testData to registry
			.then(function (testData) {
				cy.request({
					url: entityAddUrl,
					method: 'POST',
					body: testData,
					headers: {
						'api-key': apiKey,
						'content-type': 'application/json'
					}
				}).then(function (response) {
					let entityId = response.body.entityId
					cy.wrap(entityId).as('entityId');
				})
			})
		})
	})
}

function waitUntilRegistryIsCreated(registryName, count){

	cy.log('counter: ' + count)
	let statusUrl = '/registry/' + registryName + '/status'
	cy.log('waiting...')
	cy.request({
		url: statusUrl,
		failOnStatusCode: false
	}).then(function (response) {
		if(response.status === 303){
			const newCount = count + 1;
			cy.log('newCount: ' + newCount)
			if(newCount < 5){
				cy.wait(5000)
				waitUntilRegistryIsCreated(registryName, newCount)
			}
		}
	})
}
