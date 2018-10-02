//  Scenario: An API admin user deletes an existing, empty entity registry
//    Given that the API admin user is authenticated
//    And that there is an existing, empty entity registry with a schema
//    When the API admin user request deletion of an entity registry
//    Then the empty entity registry is deleted


when('the API admin user request deletion of an entity registry', () => {
	// delete empty registry
	let deleteRegistryUrl = 'https://www.unit.no';
	cy.request(deleteRegistryUrl)
})

then('the empty entity registry is deleted', () => {
	// call registry
	let registryHeartbeatUrl = 'https://www.unit.no';
	expect(cy.request(registryHeartbeatUrl))
})