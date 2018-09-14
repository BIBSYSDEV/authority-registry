when('the anonymous user requests the entity specifying an Accept header with value:', (dataTable) => {
	expect(dataTable.rawTable[0]).to.equal(
		"application/ld+json"
	)

})

then('anonymous user can view the data in the given serialization', () => {
	
})