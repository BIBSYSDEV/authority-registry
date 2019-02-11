package no.bibsys.structures;

import no.bibsys.db.SampleData;
import no.bibsys.db.structures.Registry;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;

public class RegistryTest {

    private static final String REGISTRY_NAME = "someRegistry";
    private static final Integer RANDOM_OBJECT = 2;
    private final Registry registry;

    public RegistryTest() throws IOException {
        SampleData sampleData = new SampleData();
        registry = sampleData.sampleRegistry(REGISTRY_NAME);
    }

    @Test
    public void hashCode_registryFields_hashOfAllFields() {
        assertThat(registry.hashCode(),
                is(equalTo(Objects.hash(registry.getId(), registry.getMetadata(), registry.getSchema()))));
    }

    @Test
    public void equals_anotherClass_false() {
        assertThat(registry, is(not(equalTo(RANDOM_OBJECT))));
    }

    @Test
    public void equals_null_false() {
        assertThat(registry, is(not(equalTo(null))));
    }

    @Test
    public void equals_sameObject_true() {
        assertThat(registry, is((equalTo(registry))));
    }

}
