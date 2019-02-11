package no.bibsys.web.exception;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.db.exceptions.RegistryMetadataTableBeingCreatedException;
import no.bibsys.web.model.EntityDto;
import no.bibsys.web.model.RegistryDto;

public class RegistryMetadataTableBeingCreatedExceptionMapperTest extends MapperTest<RegistryMetadataTableBeingCreatedExceptionMapper> {

    @Test 
    public void insertEntity_registryNotCreatedYet_ReturnsStatusServiceUnavailable() throws Exception {

        when(mockEntityService.addEntity(anyString(), any())).thenAnswer(invocation -> { 
            throw new RegistryMetadataTableBeingCreatedException();
        });
        
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntityRequest(registryName, expectedEntity, apiAdminKey);
        assertThat(response.getStatus(), is(Status.SERVICE_UNAVAILABLE.getStatusCode()));
    }

    @Override
    protected RegistryMetadataTableBeingCreatedExceptionMapper createMapper() {
        return new RegistryMetadataTableBeingCreatedExceptionMapper();
    }
}

