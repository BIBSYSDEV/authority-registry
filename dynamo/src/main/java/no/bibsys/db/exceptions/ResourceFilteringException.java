package no.bibsys.db.exceptions;

public class ResourceFilteringException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "The resource filter failed, list length should be 1, but was %s";

    public ResourceFilteringException(int resourceTaggingMappingListSize) {
        super(String.format(MESSAGE_TEMPLATE, resourceTaggingMappingListSize));
    }
}
