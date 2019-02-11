package no.bibsys.web.exception;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.db.exceptions.SettingValidationSchemaUponCreationException;
import no.bibsys.web.model.RegistryDto;

public class SettingValidationSchemaUponCreationExceptionMapperTest extends MapperTest<SettingValidationSchemaUponCreationExceptionMapper> {

    private String registryName;

    @Test 
    public void createRegistry_tryingToSetSchemaOnRegistryCreation_ReturnsStatusBadRequest() throws Exception {
        
        when(mockRegistryService.createRegistry(any())).thenAnswer(invocation -> { 
            throw new SettingValidationSchemaUponCreationException();
        });
        
        registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        Response response = createRegistry(registryDto, apiAdminKey);
        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
    }

    @Override
    protected SettingValidationSchemaUponCreationExceptionMapper createMapper() {
        return new SettingValidationSchemaUponCreationExceptionMapper();
    }
}

