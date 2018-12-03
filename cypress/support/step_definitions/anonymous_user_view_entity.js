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
		expect(entityData.body.entity.inScheme).to.be.equal('schemeValue')
		expect(entityData.body.entity.type).to.be.equal('typeValue')
		expect(entityData.body.entity.identifier).to.be.equal('identifierValue')
		expect(entityData.body.entity.broader).to.be.equal('broaderValue')
		expect(entityData.body.entity.perferredLabel[0].lang).to.be.equal('en')
		expect(entityData.body.entity.perferredLabel[0].value).to.be.equal('preferredLabelValue')
		expect(entityData.body.entity.alternativeLabel[0].lang).to.be.equal('en')
		expect(entityData.body.entity.alternativeLabel[0].value).to.be.equal('alternativeLabelValue')
		expect(entityData.body.entity.narrower).to.be.equal('narrowerValue')
		expect(entityData.body.entity.related).to.be.equal('relatedValue')
		expect(entityData.body.entity.definition[0].lang).to.be.equal('en')
		expect(entityData.body.entity.definition[0].value).to.be.equal('definitionValue')
		expect(entityData.body.entity.seeAlso).to.be.equal('seeAlsoValue')
	})
})