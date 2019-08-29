package no.bibsys.web.exception;

import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RegistryMetadataTableBeingCreatedExceptionMapperTest
        extends MapperTest<RegistryMetadataTableBeingCreatedExceptionMapper> {

    @Test
    public void insertEntity_registryNotCreatedYet_ReturnsStatusServiceUnavailable() throws Exception {

        when(mockEntityService.addEntity(anyString(), anyString(), any())).thenAnswer(invocation -> {
            throw new RegistryMetadataTableBeingCreatedException();
        });

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto("https://example.org/21");
        Response response = insertEntityRequest(registryName, expectedEntity, apiAdminKey);
        assertThat(response.getStatus(), is(Status.SERVICE_UNAVAILABLE.getStatusCode()));
    }

    @Override
    protected RegistryMetadataTableBeingCreatedExceptionMapper createMapper() {
        return new RegistryMetadataTableBeingCreatedExceptionMapper();
    }
}

