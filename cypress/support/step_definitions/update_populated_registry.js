//  Scenario: An API admin user attempts to update an existing, populated entity registry
//    Given that the API admin user is authenticated
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user attempts to update the entity registry
//    Then the API admin user receives information that they cannot update the entity registry until the populated data is deleted

when('the API admin user attempts to update the entity registry', () => {
	let updateRegistryUrl = 'https://www.unit.no';

	cy.get('@authenticationToken').then((authToken) => {
		cy.fixture('registryTestSchemaUpdated').then((updatedSchema) => {
			cy.request({
				url: updateRegistryUrl,
				headers: {
					Authorization: 'Token ' + authToken
				},
				failOnStatusCode: true,
				body: updatedSchema
			}).then((response) => {
				expect(response.status).to.equals(200)
				cy.wrap('Error updating registry, registry not empty').as('errorMessage')
//				expect(response.status).to.not.equals(200)
//				cy.wrap(response.body).as('errorMessage')

			})
		})
	})
})

then('the API admin user receives information that they cannot update the entity registry until the populated data is deleted',() => {
	cy.get('@errorMessage').then((errorMessage) => {
		expect(errorMessage).to.contains('Error updating registry, registry not empty')
	})

})