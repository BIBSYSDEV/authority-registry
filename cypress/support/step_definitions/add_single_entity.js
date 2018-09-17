let entityRegistryUrl = "http://ada.bibsys.no/admin/ping";
let createEntityUrl = "http://ada.bibsys.no/admin/ping";
let getEntityUrl = "http://ada.bibsys.no/admin/ping"

given('that there is an existing entity registry with a schema', () => {
	// create entity registry here?
	cy.fixture('registryTestSchema.json')
	.then((testSchema) => {
		cy.request({
			url: entityRegistryUrl,
			body: testSchema, 
			headers: {
				AuthToken: 'createTestRegistryAuthToken'
			}
		})
	})
})

when('the registry admin user requests the creation of a new entity with properly formatted data', () =>{
	cy.fixture('entityTestData.json')
	.then((testData) => {
		cy.request({
			url: createEntityUrl,
			headers: {
				AuthToken: cy.get('@authenticationToken')
			},
			body: testData
		}).then((response) => {
			// test return from create
			cy.wrap('return.id').as('returnId')
		})
	})
})

then('the entity is created', () => {
	cy.request({
		url: getEntityUrl,
//		url: getEntityUrl + cy.get('@returnId'),
		headers: {
			AuthToken: cy.get('@authenticationToken')
		}
	}).then((response) => {
		// test that entity actually exists in registry
	})
	// destroy entity registry here?
})
