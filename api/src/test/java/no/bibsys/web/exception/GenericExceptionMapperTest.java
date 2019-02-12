package no.bibsys.web.exception;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.web.model.RegistryDto;

public class GenericExceptionMapperTest extends MapperTest<BaseExceptionMapper> {

    @Test 
    public void createRegistry_genericExceptionThrown_ReturnsStatusInternalServiceError() throws Exception {

        when(mockRegistryService.createRegistry(any())).thenAnswer(invocation -> { 
            throw new Exception();
        });
        
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response response = createRegistry(registryDto, apiAdminKey);
        assertThat(response.getStatus(), is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

    @Override
    protected BaseExceptionMapper createMapper() {
        return new BaseExceptionMapper();
    }
}

