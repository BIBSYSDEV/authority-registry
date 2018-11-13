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

given('that there is an existing populated entity registry with a schema', () => {
	createEmptyRegistry();
	createTestEntity();
})

given('that there is an existing empty entity registry with a schema', () => {
	createEmptyRegistry();
})


function createEmptyRegistry(){
	let entityRegistryUrl = "/registry/";
	cy.wrap(entityRegistryUrl).as('entityRegistryUrl');

	cy.get('@registryName').then((registryName) => {
		cy.get('@apiAdminKey').then((apiAdminKey) => {
			cy.get('@entityRegistryUrl').then((url) => {
				// create new test registry
				cy.fixture('registryTestSchema.json')
				.then((testSchema) => {
					testSchema.registryName = registryName;
					let url = entityRegistryUrl + registryName
					cy.request({
						url: url,
						method: 'POST',
						body: testSchema, 
						headers: {
							'apiKey': apiAdminKey,
							'content-type': 'application/json'
						}
					}).then((response) => {
						cy.wrap(response.apiKey).as('apiKey');
					})
				})
			})
		})
	})
}

function createTestEntity(){

	cy.get('@registryName').then((registryName) => {
		let entityAddUrl = '/registry/' + registryName + '/entity';
		cy.get('@entityId').then((entityId) => {
			cy.get('@entityRegistryUrl').then((url) => {
				cy.fixture('entityTestData.json') // add testData to registry
				.then((testData) => {
					testData.id = entityId
					cy.request({
						url: entityAddUrl,
						method: 'POST',
						body: testData,
						headers: {
							'x-api-key': apiKey,
							'content-type': 'application/json'
						}
					}).then((response) => {
					})
				})
			})
		})
	})
}

