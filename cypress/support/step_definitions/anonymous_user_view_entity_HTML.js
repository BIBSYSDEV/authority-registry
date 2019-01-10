//  Scenario: An anonymous user views an entity as HTML
//    Given that there is an existing entity registry with a schema
//    And that there is an entity in the registry
//    When the anonymous user requests the entity specifying an Accept header with value text/html
//    Then anonymous user can view the data in the given format

when(/the anonymous user requests the entity specifying an Accept header with value text\/html/, () => {
	cy.log('-- anonymous_user_view_entity_HTML.js --')
	cy.get('@registryName').then((registryName) => {
		cy.get('@entityId').then((entityId) => {
			const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
		
			cy.visit(getEntityUrl)
		})
	})
})

then('anonymous user can view the data in the given format', () => {
	cy.get('@registryName').then((registryName) => {
		cy.get('@entityId').then((entityId) => {
			const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
			cy.visit(getEntityUrl)

			cy.get('li[data-automation-id=name]').contains('nameValue')
			cy.get('li[data-automation-id=identifier]').contains('identifierValue')
			cy.get('li[data-automation-id=inScheme]').contains('schemeValue')
			cy.get('li[data-automation-id=type]').contains('typeValue')
			cy.get('li[data-automation-id=broader]').contains('broaderValue')
			cy.get('li[data-automation-id=preferredLabel]').contains('preferredLabelValue')
			cy.get('li[data-automation-id=alternativeLabel]').contains('alternativeLabelValue')
			cy.get('li[data-automation-id=narrower]').contains('narrowerValue')
			cy.get('li[data-automation-id=related]').contains('relatedValue')
			cy.get('li[data-automation-id=definition]').contains('definitionValue')
			cy.get('li[data-automation-id=seeAlso]').contains('seeAlsoValue')
		})
	})
})