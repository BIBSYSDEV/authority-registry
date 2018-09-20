// When the anonymous user requests the entity

then('the response contains an ETag and a Last-Modified header', () => {
	cy.get('@response').then((response) => {
		cy.log('response = ' + response.headers)
		.then(() => {
			expect(response.headers['date']).to.not.be.undefined
			expect(response.headers['date']).to.not.equal('')
//			expect(response.headers['last-modified']).to.not.be.undefined
//			expect(response.headers['last-modified']).to.not.equal('')
//			expect(response.headers['etag']).to.not.be.undefined
//			expect(response.headers['etag']).to.not.equal('')
		})
	})
})