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

given('that there is an existing, populated entity registry with a schema', () => {
	createEmptyRegistry();
	createTestEntity();
})

given('that there is an existing, empty entity registry with a schema', () => {
	createEmptyRegistry();
})


function createEmptyRegistry(){
//	let entityRegistryUrl = "/registry/create";
	let entityRegistryUrl = "http://ada.bibsys.no/admin/ping";
	cy.wrap(entityRegistryUrl).as('entityRegistryUrl')
	let entityGetUrl = "http://ada.bibsys.no/admin/ping";
	cy.wrap(entityGetUrl).as('entityGetUrl')

	let uuid = require('uuid');
	let randomRegistryName = uuid.v4();
	cy.wrap(randomRegistryName).as('registryName');

	cy.get('@entityRegistryUrl').then((url) => {
		// create new test registry
		cy.fixture('registryTestSchema.json')
		.then((testSchema) => {
			cy.get('@registryName').then((registryName) => {
				testSchema.registryName = registryName;
				cy.request({
					url: url,
//					method: 'POST',
					body: testSchema, 
					headers: {
						Authorization: 'Token API_admin_token',
						'content-type': 'application/json'
					}
				})
			})
		})
	})
}

function createTestEntity(){
	let entityAddUrl = "http://ada.bibsys.no/admin/ping";
//	let entityAddUrl = '/registry/';
	let entityId = '0';
	cy.wrap(entityId).as('entityId')

	cy.get('@registryName').then((registryName) => {
		entityAddUrl += registryName + '/put'; 

		cy.fixture('entityTestData.json') // add testData to registry
		.then((testData) => {
			cy.request({
				url: entityAddUrl,
//				method: 'PUT',
				body: testData,
				headers: {
					Authorization: 'Token API_admin_token'
				}
			}).then((response) => {
				cy.wrap(entityId).as('entityId')
			})
		})
	})
}

