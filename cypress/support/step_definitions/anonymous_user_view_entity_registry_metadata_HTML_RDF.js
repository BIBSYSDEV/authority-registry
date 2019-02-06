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

When('an anonymous user dereferences the base URI for the registry specifying mediatypes:', (dataTable) => {
  cy.log('-- anonymous_user_view_entity_registry_metadata_HTML_RDF.js --');

  cy.wrap('rdf').as('type');
  const formats = dataTable.rawTable;
  cy.wrap(formats).as('formats');
});

// Scenario: An anonymous user views the metadata for a registry as HTML
//    Given that there is an existing populated entity registry with a schema
//    When an anonymous user dereferences the base URI for the registry specifying mediatype text/html
//    Then they see metadata related to the entity registry regarding:
//      | Registry name                    |
//      | Registry type                    |
//      | Publisher                        |
//      | License for the data             |
//      | Owner organisation               |
//      | Participating organisations      |
//      | Languages used in dataset        |
//      | Creation date                    |
//      | Modification date                |
//      | Relations to other data sets     |
//      | Location of APIs                 |
//      | Example resources                |
//      | Base URI for dataset             |
//      | Location of SPARQL endpoint      |
//      | Description of available formats |

When(/an anonymous user dereferences the base URI for the registry specifying mediatype text\/html/, (dataTable) => {
  cy.log('-- anonymous_user_view_entity_registry_metadata_HTML_RDF.js --');

  cy.get('@registryName').then((registryName) => {
    cy.wrap('html').as('type');
    cy.visit('/registry/' + registryName);
  });
});

Then('they see metadata related to the entity registry regarding:', () => {
  cy.get('@registryName').then((registryName) => {
    const registryEndpoint = '/registry/' + registryName;
    cy.get('@type').then((type) => {
      if (type === 'rdf') {
        testRdf(registryName, registryEndpoint);
      } else {
        checkHtml(registryName);
      }
    });
  });
});

function testRdf(registryName, registryEndpoint) {
  cy.log('testing rdf');
  cy.get('@formats').then((formats) => {
    formats.forEach(format => {
      const mediaType = format[0];
      cy.log('mediatype = ' + mediaType);
      const fileName = 'tests_registry.' + mediaType.replace('application/', '').replace('+', '');
      cy.fixture(fileName).then((testData) => {
        cy.request({
          url: registryEndpoint,
          headers: {
            Accept: mediaType,
          },
        }).then((response) => {
          switch (mediaType) {
            default:
            case 'application/json':
            case 'application/ld+json':
              if (typeof testData === 'object') {
                expect(JSON.stringify(response.body)).to.deep.equal(
                    JSON.stringify(testData));
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
}

function checkAgainstTestData(testData, response) {
  const tests = testData.split(EOL);
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