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
		
			cy.request({
				url: getEntityUrl,
				headers: {
					Accept: 'text/html'
				}
			}).then((response) => {
				cy.server()
				cy.route('GET', getEntityUrl, response)
			})
		})
	})
})

then('anonymous user can view the data in the given format', () => {
	cy.get('@registryName').then((registryName) => {
		cy.get('@entityId').then((entityId) => {
			const getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
			cy.visit(getEntityUrl)
			
			cy.contains('preferredLabelValue')
//			cy.get('@htmlResponse').then((response) => {
//			expect(response.body).to.have.string('<html>')
//			expect(response.body).to.have.string('<body>')
//			expect(response.body).to.have.string('<li data-automation-id="preferredLabel">')
		})
	})
})