//  Scenario: An anonymous user views API information
//    Given that there is an existing entity registry with a schema
//    When an anonymous user requests the OpenAPI documentation
//    Then the OpenAPI documentation is returned

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('an anonymous user requests the OpenAPI documentation', () => {
  cy.log('-- anonymous_user_view_API_information.js --');
  const openApiDocumentationUrl = 'https://www.unit.no';
  cy.request(openApiDocumentationUrl).then((response) => {
    cy.wrap(response.body).as('documentation');
  });

});

Then('the OpenAPI documentation is returned', () => {
  cy.get('@documentation').then((documentation) => {
    //		expect(documentation).to.contain('test registry')
  });
});
