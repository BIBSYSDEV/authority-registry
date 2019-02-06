//  Scenario: An anonymous user views an entity specifying a specific format
//    And that there is an entity in the registry
//    When the anonymous user requests the entity with format:
//
//      | application/marcxml+xml |
//      | application/marc        |
//      | application/marcxml     |
//    Then anonymous user can view the data in the format

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the anonymous user requests the entity with format:', (dataTable) => {
  cy.log('-- anonymous_user_view_entity_MARC.js --');
  const formats = dataTable.rawTable;
  cy.wrap(formats).as('formats');
});

Then('anonymous user can view the data in the format', () => {
  cy.get('@registryName').then((registryName) => {
    cy.get('@registryName').then((registryName) => {
      cy.get('@entityId').then((entityId) => {
        const getUrl = 'registry/' + registryName + '/entity/' + entityId;
        cy.get('@formats').then((formats) => {
          formats.forEach(format => {
            const formatType = format[0];
            const testMarcXml = 'marc:record';
            const testDataField = '<marc:datafield tag="100" ind1=" " ind2=" ">';
            const testSubfield = '<marc:subfield code="a">norsklabel</marc:subfield>';
            cy.request({
              url: getUrl,
              headers: {
                Accept: formatType,
              },
            }).then((response) => {
              expect(response.body).to.contain(testMarcXml);
              expect(response.body).to.contain(testDataField);
              expect(response.body).to.contain(testSubfield);
            });
          });
        });
      });
    });
  });
});