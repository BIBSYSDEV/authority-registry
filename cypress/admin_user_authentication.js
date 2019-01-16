import { Given, When, Then } from 'cypress-cucumber-preprocessor/steps';

//  Scenario: An API admin user authenticates themselves
//    Given that there is an API admin user with valid credentials
//    When they provide these credentials
//    Then they are authenticated and receive a valid authentication token

const authenticationUrl = '/'; // authentication service here
let credentials = '';
let authenticated = 'not authenticated';

Given('that there is an API admin user with valid credentials', () => {
  credentials = 'API admin user credentials';
});

When('they provide these credentials', () => {
  cy.request(authenticationUrl, credentials)
    .then(() => { // check if authenticated
      authenticated = 'authenticated';
      cy.wrap(authenticated).as('authenticated');
    });
});

Then('they are authenticated and receive a valid authentication token', () => {
  cy.get('@authenticated').should('equal', 'authenticated');
});
