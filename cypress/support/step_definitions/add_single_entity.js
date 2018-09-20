let createEntityUrl = "http://ada.bibsys.no/admin/ping";
let getEntityUrl = "http://ada.bibsys.no/admin/ping";

when('the registry admin user requests the creation of a new entity with properly formatted data', () =>{
	cy.wrap('').as('returnId')
	cy.fixture('entityTestData.json')
	.then((testData) => {
		cy.get('@authenticationToken').then((authToken) => {
			cy.request({
				url: createEntityUrl,
				headers: {
					Authorization: 'Token ' + authToken
				},
				body: testData
			}).then((response) => {
				// test return from create
				cy.wrap('return.id').as('returnId')
			})
		})
	})
})

then('the entity is created', () => {
	cy.get('@returnId').then((returnId) => {

		cy.request(getEntityUrl)
//		cy.request(getEntityUrl + returnId)
	})
})
