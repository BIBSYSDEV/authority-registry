/* global defineParameterType, Given, When, Then */

given('no cleanup', () =>{
	cy.log('-- api_admin__common.js --')
	cy.wrap(false).as('cleanUp');
})
