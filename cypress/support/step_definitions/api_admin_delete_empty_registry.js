//Scenario: An API admin user deletes an existing, empty entity registry
//Given that the API admin user has a valid API key for API administration
//And that there is an existing, empty entity registry with a schema
//When the API admin user uses the API key and requests deletion of an entity registry
//Then the empty entity registry is deleted


when('the API admin user uses the API key and requests deletion of an entity registry', () => {
	// delete empty registry
	cy.get("@registryName").then((registryName) => {
		cy.get("@registryApiKey").then((apiKey) => {

			let deleteRegistryUrl = '/registry/' + registryName;
			cy.request({
				url: deleteRegistryUrl,
				method: "DELETE",
				headers: {
					'content-type': 'application/json',
					'apikey': apiKey
				}
			})
		})
	})
})

then('the empty entity registry is deleted', () => {
	// call registry
	cy.get("@registryName").then((registryName) => {

		let registryHeartbeatUrl = '/registry/' + registryName;
		cy.request({
			url: registryHeartbeatUrl,
			method: "GET",
			failOnStatusCode: false
		}).then((response) => {
			expect(response.status).equals(404)
		})
	})
})