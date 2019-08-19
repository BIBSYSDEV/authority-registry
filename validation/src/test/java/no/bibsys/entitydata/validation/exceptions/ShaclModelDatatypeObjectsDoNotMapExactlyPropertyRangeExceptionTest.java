package no.bibsys.entitydata.validation.exceptions;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeExceptionTest {

    private static final String EXPECTED_MESSAGE = "The ShaCL model provided attempts to match non-ontology property "
            + "ranges: %n%n %s";
    private static final String SOME_TURTLE = "Some TURTLE";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testThrownExceptionAndMessage() throws ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException {
        expectedException.expect(ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException.class);
        expectedException.expectMessage(String.format(EXPECTED_MESSAGE, SOME_TURTLE));

        throw new ShaclModelDatatypeObjectsDoNotMapExactlyPropertyRangeException(SOME_TURTLE);

    }
}