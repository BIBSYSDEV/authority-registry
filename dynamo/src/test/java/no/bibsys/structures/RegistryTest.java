package no.bibsys.structures;

import com.fasterxml.jackson.databind.node.ObjectNode;
import no.bibsys.db.SampleData;
import no.bibsys.db.structures.Registry;
import no.bibsys.utils.JsonUtils;
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
    private static final String RANDOM_FIELD = "aField";
    private static final String RANDOM_VALUE = "aValue";
    private final Registry registry;
    private final SampleData sampleData;

    public RegistryTest() throws IOException {
        sampleData = new SampleData();
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

    @Test
    public void equals_equalObject_true() {
        assertThat(registry, is((equalTo(sampleData.sampleRegistry(REGISTRY_NAME)))));
    }

    @Test
    public void equals_notEqualObject_false() {
        Registry anotherRegistry = sampleData.sampleRegistry(REGISTRY_NAME);
        ObjectNode node = JsonUtils.newJsonParser().createObjectNode();
        node.put(RANDOM_FIELD, RANDOM_VALUE);
        anotherRegistry.setMetadata(node);

        assertThat(registry, is(not(equalTo(anotherRegistry))));
    }

}
