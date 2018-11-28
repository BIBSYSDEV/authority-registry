//  Scenario: An API admin user attempts to update the validation schema of an existing, populated entity registry
//    Given that the API admin user has a valid API key for API administration
//    And that there is an existing, populated entity registry with a schema
//    When the API admin user uses the API key and submits a request to update the validation schema of the entity registry
//    Then the API admin user receives information that they cannot update the entity registry validation schema until the populated data is deleted

then('the API admin user receives information that they cannot update the entity registry validation schema until the populated data is deleted',() => {
	cy.get('@updateSchemaResponse').then((response) => {
		expect(response.status).to.equal(405)
	})

})