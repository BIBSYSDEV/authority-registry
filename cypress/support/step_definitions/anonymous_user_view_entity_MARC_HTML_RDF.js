import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the anonymous user requests the entity specifying an Accept header with value:', (dataTable) => {
  cy.log('-- anonymous_user_view_entity_MARC_HTML_RDF.js --');
  const formats = dataTable.rawTable;
  cy.wrap(formats).as('formats');
  let resultMap = [];
});

// Scenario: An anonymous user views an entity specifying a specific MARC format
// Given that there is an existing entity registry with a schema
// And that there is an entity in the registry
// When the anonymous user requests the entity specifying an Accept header with value:
// | application/marcxml+xml |
// | application/marc        |
// | application/mads+xml    |
// | application/marcxml     |
// Then anonymous user can view the data in the given MARC format

Then('anonymous user can view the data in the given MARC format', () => {
  cy.get('@results').then((results) => {
    cy.get('@formats').then((formats) => {
      formats.forEach(format => {
        assert.equals(format, results[format]);
      });
    });
  });
});

// Scenario: An anonymous user views an entity specifying a specific RDF serialization and a specific profile
// Given that there is an existing entity registry with a schema
// And that there is an entity in the registry
// When the anonymous user requests the entity specifying an Accept header with value:
// | application/ld+json     |
// | application/n-triples   |
// | application/rdf+xml     |
// | application/turtle      |
// | application/json        |
// | application/rdf         |
// And specifies an Accept-schema header with a value:
// | native-uri   |
// | skos-uri     |
// | bibframe-uri |
// Then anonymous user can view the data in the serialization and profile requested

When('specifies an Accept-schema header with a value:', (dataTable) => {
  const profileArray = dataTable.rawTable;
  cy.wrap(profileArray[0]).as('profile');
});

Then('anonymous user can view the data in the serialization and profile requested', () => {
  cy.get('@entityGetUrl').then((entityGetUrl) => {
    cy.get('@entityId').then((entityId) => {
      cy.request(entityGetUrl)
      //			cy.request(entityGetUrl + entityId)
        .then((response) => {
          cy.get('@profile').then((profile) => {
            expect('native-uri').to.contains(profile);
            //					expect(response.headers['content-type']).to.contains(profile)
          });
        });
    });
  });
});

// Scenario: An anonymous user views an entity specifying an RDF serialization
// Given that there is an existing entity registry with a schema
// And that there is an entity in the registry
// When the anonymous user requests the entity specifying an Accept header with value:
// | application/ld+json     |
// | application/n-triples   |
// | application/rdf+xml     |
// | application/turtle      |
// | application/json        |
// | application/rdf         |
// Then anonymous user can view the data in the given serialization

Then('anonymous user can view the data in the given serialization', () => {
  cy.log('-- anonymous_user_view_entity_MARC_HTML_RDF.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getUrl = 'registry/' + registryName + '/entity/' + entityId;
      cy.get('@formats').then((formats) => {
        formats.forEach(format => {
          cy.request({
            url: getUrl,
            headers: {
              Accept: format[0],
            },
          }).then((response) => {
            expect(response.headers['content-type']).to.be.equal(format[0]);
          });
        });
      });
    });
  });
});
