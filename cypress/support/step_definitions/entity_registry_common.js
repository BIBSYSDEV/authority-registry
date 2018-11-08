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

	cy.get('@registryName').then((registryName) => {
		cy.get('@entityRegistryUrl').then((url) => {
			// create new test registry
			cy.fixture('registryTestSchema.json')
			.then((testSchema) => {
				testSchema.registryName = registryName;
				let url = entityRegistryUrl + registryName
				cy.request({
					url: url,
					method: 'PUT',
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

	cy.get('@registryName').then((registryName) => {
		let entityAddUrl = '/registry/' + registryName + '/entity';

		cy.get('@entityId').then((entityId) => {
			cy.fixture('entityTestData.json') // add testData to registry
			.then((testData) => {
				testData.id = entityId
				cy.request({
					url: entityAddUrl,
					method: 'POST',
					body: testData,
					headers: {
						Authorization: 'Token API_admin_token',
						'content-type': 'application/json'
					}
				}).then((response) => {
				})
			})
		})
	})
}

