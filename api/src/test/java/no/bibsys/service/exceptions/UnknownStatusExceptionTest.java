package no.bibsys.service.exceptions;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.web.exception.MapperTest;
import no.bibsys.web.model.RegistryDto;

public class UnknownStatusExceptionTest extends MapperTest<UnknownStatusExceptionMapper> {

    @Test 
    public void createRegistry_registryInUnknownStatus_ReturnsStatusInternalServerError() throws Exception {
        
        when(mockRegistryService.createRegistry(any())).thenAnswer(invocation -> { 
            throw new UnknownStatusException("This is a test");
        });
        
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response response = createRegistry(registryDto, apiAdminKey);

        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.readEntity(String.class), is("Registry in unknown status."));
    }

    @Override
    protected UnknownStatusExceptionMapper createMapper() {
        return new UnknownStatusExceptionMapper();
    }

}
