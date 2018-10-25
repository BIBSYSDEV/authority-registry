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
	let entityRegistryUrl = "/registry/";
	cy.wrap(entityRegistryUrl).as('entityRegistryUrl');
	cy.wrap('https://www.unit.no').as('entityGetUrl');

	cy.get('@entityRegistryUrl').then((url) => {
		// create new test registry
		cy.fixture('registryTestSchema.json')
		.then((testSchema) => {
			cy.get('@registryName').then((registryName) => {
				testSchema.registryName = registryName;
				let url = entityRegistryUrl + registryName
				cy.request({
					url: url,
					method: 'PUT',
					body: testSchema, 
					headers: {
						'phase': 'test',
						Authorization: 'Token API_admin_token',
						'content-type': 'application/json'
					}
				})
			})
		})
	})
}

function createTestEntity(){
	let entityAddUrl = '/registry/';
//	let entityAddUrl = 'https://www.unit.no';
	let entityId = '0';
	cy.wrap(entityId).as('entityId')

	cy.get('@registryName').then((registryName) => {
		entityAddUrl += registryName; 

		cy.fixture('entityTestData.json') // add testData to registry
		.then((testData) => {
			cy.request({
				url: entityAddUrl,
				method: 'POST',
				body: testData,
				headers: {
					'phase': 'test',
					Authorization: 'Token API_admin_token',
					'content-type': 'application/json'
				}
			}).then((response) => {
				cy.wrap(entityId).as('entityId')
			})
		})
	})
}

