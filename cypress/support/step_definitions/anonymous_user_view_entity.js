//Scenario: An anonymous user views an entity without specifying a format
//Given that there is an existing entity registry with a schema
//And that there is an entity in the registry
//When the anonymous user requests the entity
//Then anonymous user can view the entity's data in the native database format

when('the anonymous user requests the entity', () => {
	cy.log('-- anonymous_user_view_entity.js --')
	
	cy.get('@registryName').then((registryName) => {

		cy.get('@entityId').then((entityId) => {
			cy.wrap('').as('response')
			let getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
			cy.request(getEntityUrl).then((response) => {
				cy.wrap(response).as('response')
			})
		})
	})
})

then("anonymous user can view the entity's data in the native database format", () => {
	cy.get('@response').then((entityData) => {
		expect(entityData.body.name).to.be.equal('nameValue')
	})
})