// Scenario: An anonymous user views an entity without specifying a format
// Given that there is an existing entity registry with a schema
// And that there is an entity in the registry
// When the anonymous user requests the entity
// Then anonymous user can view the entity's data in the native database format

import {Then, When} from 'cypress-cucumber-preprocessor/steps';

When('the anonymous user requests the entity', () => {
  cy.log('-- anonymous_user_view_entity.js --');

  cy.get('@registryName').then((registryName) => {

    cy.get('@entityId').then((entityId) => {
      cy.wrap('').as('response');
      let getEntityUrl = '/registry/' + registryName + '/entity/' + entityId;
      cy.request({
        url: getEntityUrl,
        headers: {
          accept: 'application/json',
        },
      }).then((response) => {
        cy.wrap(response).as('response');
      });
    });
  });
});

Then("anonymous user can view the entity's data in the native database format", () => {
  cy.get('@response').then((entityData) => {
    expect(entityData.body.body['@type']).to.be.equal('unit:Concept');
    // expect(entityData.body.body.inScheme).to.be.equal('schemeValue');
    // expect(entityData.body.body.type).to.be.equal('typeValue');
    // expect(entityData.body.body.identifier).to.be.equal('identifierValue');
    // expect(entityData.body.body.broader).to.be.equal('broaderValue');
    // expect(entityData.body.body.narrower[0]).to.be.equal('narrowerValue');
    // expect(entityData.body.body.related[0]).to.be.equal('relatedValue');
    // expect(entityData.body.body.seeAlso[0]).to.be.equal('seeAlsoValue');
    // expect(entityData.body.body.preferredLabel[0]['@language']).to.be.equal('en');
    // expect(entityData.body.body.preferredLabel[0]['@value']).to.be.equal('preferredLabelValue');
    // expect(entityData.body.body.alternativeLabel[0]['@language']).to.be.equal('en');
    // expect(entityData.body.body.alternativeLabel[0]['@value']).to.be.equal('alternativeLabelValue');
    // expect(entityData.body.body.definition[0]['@language']).to.be.equal('en');
    // expect(entityData.body.body.definition[0]['@value']).to.be.equal('definitionValue');
  });
});
