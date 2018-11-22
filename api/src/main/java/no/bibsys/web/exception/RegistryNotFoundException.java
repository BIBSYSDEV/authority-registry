package no.bibsys.web.exception;

public class RegistryNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 771312714770513329L;

	public RegistryNotFoundException(String registryName) {
		super(String.format("Registry with name %s does not exist", registryName));
	}
	
}
