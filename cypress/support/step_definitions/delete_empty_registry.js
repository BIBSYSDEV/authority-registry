//  Scenario: An API admin user deletes an existing, empty entity registry
//    Given that the API admin user is authenticated
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user request deletion of an entity registry
//    Then the empty entity registry is deleted


when('the API admin user request deletion of an entity registry', () => {
	// delete empty registry
	cy.get("@registryName").then((registryName) => {
		let deleteRegistryUrl = '/registry/' + registryName;
		cy.request({
			url: deleteRegistryUrl,
			method: "DELETE",
			headers: {
				'phase': 'test',
				'content-type': 'application/json'
			}
		})
	})
})

then('the empty entity registry is deleted', () => {
	// call registry
	cy.get("@registryName").then((registryName) => {
		
		let registryHeartbeatUrl = '/registry/' + registryName;
		cy.request({
			url: registryHeartbeatUrl,
			method: "DELETE",
			failOnStatusCode: false
		}).then((response) => {
			expect(response.status).equals(404)
		})
	})
})