import {Then, When} from 'cypress-cucumber-preprocessor/steps';
import EOL from 'os';

When(
  'the anonymous user requests the entity specifying an Accept header with value:',
  (dataTable) => {
    cy.log('-- anonymous_user_view_entity_HTML_RDF.js --');
    const formats = dataTable.rawTable;
    cy.wrap(formats).as('formats');
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

Then(
  'anonymous user can view the data in the serialization and profile requested',
  () => {
    cy.get('@entityGetUrl').then((entityGetUrl) => {
      cy.get('@entityId').then((entityId) => {
        cy.request(entityGetUrl)
          .then((response) => {
            cy.get('@profile').then((profile) => {
              expect('native-uri').to.contains(profile);
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
  cy.log('-- anonymous_user_view_entity_HTML_RDF.js --');
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getUrl = 'registry/' + registryName + '/entity/' + entityId;
      cy.get('@formats').then((formats) => {
        formats.forEach(format => {
          const formatType = format[0];
          const fileName = 'tests.' + formatType.replace('application/',
            '').replace('+', '');
          cy.fixture(fileName).then((testData) => {
            cy.request({
              url: getUrl,
              headers: {
                Accept: formatType,
              },
            }).then((response) => {

              switch (formatType) {
                default:
                case 'application/json':
                case 'application/ld+json':
                  if (typeof testData === 'object') {
                    expect(JSON.stringify(response.body.body)).to.deep.equal(
                      JSON.stringify(testData.body));
                  } else {
                    expect(JSON.stringify(
                      JSON.parse(response.body))).to.deep.equal(
                      JSON.stringify(JSON.parse(testData)));
                  }
                  break;
                case 'application/rdf':
                case 'application/rdf+xml':
                case 'application/n-triples':
                case 'application/turtle':
                  checkAgainstTestData(testData, response);
              }

            });
          });
        });
      });
    });
  });
});

function checkAgainstTestData(testData, response) {
  const tests = testData.split(EOL);
  tests.forEach((test) => {
    expect(response.body).to.contain(test);
  });

}
