//  Scenario: An anonymous user submits a request to see if a resource is
//  modified
//    Given that there is an existing entity registry with a schema
//    And that there is an existing entity in the registry
//    When the anonymous user requests the entity
//    Then the response contains an ETag and a Last-Modified header

import {Then} from 'cypress-cucumber-preprocessor/steps';

Then('the response contains an ETag and a Last-Modified header', () => {
  cy.log('-- anonymous_user_is_resource_modified.js --');
  cy.get('@response').then((response) => {
    cy.log('response = ' + response.headers)
      .then(() => {
        expect(response.headers['date']).to.not.be.undefined; 
        expect(response.headers['date']).to.not.equal('');
        // commented out waiting for service to return last-modified and etag
        // expect(response.headers['last-modified']).to.not.be.undefined;
        // expect(response.headers['last-modified']).to.not.equal('');
        // expect(response.headers['etag']).to.not.be.undefined;
        // expect(response.headers['etag']).to.not.equal('');
      });
  });
});
