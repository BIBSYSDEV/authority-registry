/* global given */

given('that there is an existing entity registry with a schema', () => {
	cy.log('creating empty registry')
	createEmptyRegistry(true);
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

//			waitUntilRegistryIsCreated(registryName);
			
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

let ready = false

function waitUntilRegistryIsCreated(registryName){

	let statusUrl = '/registry/' + registryName + '/status'
	cy.log('waiting...')
	var count = 0;
	do{ 
		cy.request({
			url: statusUrl,
			failOnStatusCode: false
		}).then(function (response) {
			if(response.status === 200){
				ready = true

			}
			cy.log('status = ' + response.status + ' ready = ' + ready)
		})
		cy.log('ready =' + ready)

		if(!ready){
			cy.wait(2000);
		}

		if(count++ > 5){
			ready = true;
		}

		cy.log('ready = ' + ready + ', count = ' + count)
	}while(!ready)
}
