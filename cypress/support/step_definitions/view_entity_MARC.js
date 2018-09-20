when('the anonymous user requests the entity specifying an Accept header with value:',(dataTable) => {
	
	let rawTable = dataTable.rawTable;
	let rdfArray = ['application/ld+json', 'application/n-triples', 'application/rdf+xml', 'application/turtle', 'application/json', 'application/rdf']
	let marcArray = ['application/marcxml+xml', 'application/marc', 'application/mads+xml', 'application/marcxml']
	
	let format = ''
	if(rawTable.length === rdfArray.length && rawTable.every(function(value, index) { return value == rdfArray[index]})){
		format = 'application/ld+json'
	}else{
		if(rawTable.length === marcArray.length && rawTable.every(function(value, index) { return value == marcArray[index]})){
			format = 'application/marcxml+xml'
		}
	}
	expect(format).to.not.equals('')
	cy.wrap(format).as('format')
	
	cy.get('@entityId').then((entityId) => {
		cy.get('@entityGetUrl').then((getUrl) => {
//			getUrl += entityId
			cy.request({
				url: getUrl,
				headers: {
					'Accept': format
				}
			}).then((response) => {
				cy.wrap(response).as('getResponse')
			})
			
		})
	})

})

then('anonymous user can view the data in the given MARC format', () => {
	cy.get('@getResponse').then((response) => {
		cy.get('@format').then((format) => {
//			expect(response.headers['content-type']).contains(format)
//			test response body for something?
//			set up multiple tests for all the formats?
		})
	})
})