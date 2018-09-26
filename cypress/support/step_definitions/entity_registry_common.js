/* global given */

given('that there is an existing entity registry with a schema', () => {
	let entityRegistryUrl = "http://ada.bibsys.no/admin/ping";
	cy.wrap(entityRegistryUrl).as('entityRegistryUrl')
	let entityGetUrl = "http://ada.bibsys.no/admin/ping";
	cy.wrap(entityGetUrl).as('entityGetUrl')
	
	cy.wrap('').as('registryName')
	
	cy.get('@entityRegistryUrl').then((url) => {
		// Destroy any existing test registry
		
		// create new test registry
		cy.fixture('registryTestSchema.json')
		.then((testSchema) => {
			cy.request({
				url: url,
//				method: 'POST',
				body: testSchema, 
				headers: {
					Authorization: 'Token API_admin_token'
				}
			}).then((response) => {
				// return registry id, maybe?
				cy.wrap('testRegistryName').as('registryName')
			})
		})
	})
})

function createTestEntity(){
	let entityAddUrl = "http://ada.bibsys.no/admin/ping";
	let entityId = '0';
	cy.wrap(entityId).as('entityId')
	
	cy.fixture('entityTestData.json') // add testData to registry
	.then((testData) => {
		cy.request({
			url: entityAddUrl,
//			method: 'POST',
			body: testData,
			headers: {
				Authorization: 'Token API_admin_token'
			}
		}).then((response) => {
			cy.wrap(entityId).as('entityId')
		})
	})
}

given('that there is an entity in the registry', () => {
	createTestEntity()
})

given('that there is an existing entity in the registry', () => {
	createTestEntity()
})

