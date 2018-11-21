package no.bibsys.db.exceptions;

public class EntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1402358828817933866L;

	public EntityNotFoundException(String registryId, String entityId) {
		super(String.format("Entity with id %s does not exist in registry %s", entityId, registryId));
	}

}
