package no.bibsys.web.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;

public class EntityDtoTest {

    private static final String PATH = "path";
    private static final String BODY = "body";

    @Test
    public void toString_entityDtoHasData_returnsString() {
        EntityDto entityDto = new EntityDto();
        entityDto.setBody(BODY);
        entityDto.setId(UUID.randomUUID().toString());
        entityDto.setPath(PATH);
        
        assertThat(entityDto.toString(), is(not("")));
    }

    @Test
    public void hashCode_entityDtoHasData_returnsSomeHashCode() {
        EntityDto entityDto = new EntityDto();
        entityDto.setBody(BODY);
        entityDto.setId(UUID.randomUUID().toString());
        entityDto.setPath(PATH);
        
        assertThat(entityDto.hashCode(), is(not(0)));
    }
}
