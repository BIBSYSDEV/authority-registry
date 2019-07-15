package no.bibsys.db.exceptions;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ResourceFilteringExceptionTest {
    private static final int TWENTY_THREE = 23;
    private static final String EXPECTED_MESSAGE_TEMPLATE =
            "The resource filter failed, list length should be 1, but was %s";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testException() {
        expectedException.expect(ResourceFilteringException.class);
        expectedException.expectMessage(String.format(EXPECTED_MESSAGE_TEMPLATE, TWENTY_THREE));
        throw new ResourceFilteringException(TWENTY_THREE);
    }

}