// Scenario: An anonymous user views the metadata for a registry as RDF
//    Given that there is an existing populated entity registry with a schema
//    When an anonymous user dereferences the base URI for the registry specifying mediatypes:
//      | application/ld+json     |
//      | application/n-triples   |
//      | application/rdf+xml     |
//      | application/turtle      |
//      | application/json        |
//      | application/rdf         |
//    Then they see metadata related to the entity registry regarding:
//      | Metatata                |
//      | Available data profiles |

import {Then, When} from 'cypress-cucumber-preprocessor/steps';
import EOL from 'os';

When('an anonymous user dereferences the base URI for the registry specifying mediatypes:',
  (dataTable) => {
    cy.log('-- anonymous_user_view_entity_registry_metadata_RDF.js --');

    const mediaTypeArray = dataTable.rawTable;
    cy.wrap(mediaTypeArray).as('mediaTypes');
  });

Then('they see metadata related to the entity registry regarding:', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@entityId').then((entityId) => {
      const getUrl = 'registry/' + registryName;
      cy.get('@mediaTypes').then((mediaTypes) => {
        mediaTypes.forEach(mediaTypeArr => {
          const mediaType = mediaTypeArr[0];
          cy.log('mediaType = ' + mediaType)
          const fileName = 'tests_registry.' + mediaType.replace('application/',
            '').replace('+', '');
          cy.fixture(fileName).then((testData) => {
            cy.request({
              url: getUrl,
              headers: {
                Accept: mediaType,
              },
            }).then((response) => {

              switch (mediaType) {
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
                  break;
                case 'text/html':
                  checkHtml(registryName);
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
  debugger;
  tests.forEach((test) => {
    expect(response.body).to.contain(test);
  });

}

function checkHtml(registryName) {
  cy.visit('/registry/' + registryName);

  cy.contains(registryName);

  cy.get('li[data-automation-id=name]').contains('nameValue');
  cy.get('li[data-automation-id=registryName]').contains('registryNameValue');
  cy.get('li[data-automation-id=description]').contains('descriptionValue');
}