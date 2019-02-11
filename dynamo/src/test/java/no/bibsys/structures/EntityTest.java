package no.bibsys.structures;

import no.bibsys.db.SampleData;
import no.bibsys.db.structures.Entity;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public class EntityTest {

    private final SampleData sampleData;
    private final Entity entity;

    public EntityTest() throws IOException {
        sampleData = new SampleData();
        entity = sampleData.sampleEntity();
    }

    @Test
    public void hashCode_entityA_bodyHashCode() {
        assertThat(entity.hashCode(), is(equalTo(Objects.hashCode(entity.getBody()))));
    }

    @Test
    public void isEqual_anotherEntityBWithSameBody_true() throws IOException {
        Entity entity2 = sampleData.sampleEntity();
        entity2.setCreated("createdagain");
        entity2.setId("anotherId");

        assertThat(entity, is(equalTo(entity2)));
    }

    @Test
    public void isEqual_notWithNotNull_false() {
        boolean equals = entity.equals(null);
        assertThat(equals, is(equalTo(false)));
    }

    @Test
    public void isEqual_sameObject_true() {
        Entity entity2 = entity;
        boolean equals = entity.equals(entity2);
        ;
        assertThat(equals, is(equalTo(true)));
    }

    @Test
    public void isEqual_notEntity_false() {
        Integer sameInt = 2;
        boolean equals = entity.equals(sameInt);
        assertThat(equals, is(equalTo(false)));
    }

    @Test
    public void toString_void_notNullString() {
        assertThat(entity.toString(), is(not(equalTo(null))));
    }

}
