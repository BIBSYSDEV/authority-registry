package no.bibsys.web.exception.validationexceptionmappers;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.utils.exception.ValidationSchemaSyntaxErrorException;
import no.bibsys.web.exception.MapperTest;
import no.bibsys.web.model.RegistryDto;

public class ValidationSchemaSyntaxErrorExceptionTest
        extends MapperTest<ValidationSchemaSyntaxErrorExceptionMapper> {

    @Test 
    public void updateSchema_syntaxErrorInSchema_ReturnsStatusBadRequest() throws Exception {
        
        when(mockRegistryService.updateRegistrySchema(anyString(), anyString())).thenAnswer(invocation -> { 
            throw new ValidationSchemaSyntaxErrorException(null);
        });
        
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        Response response = putSchema(registryName, validValidationSchema);
        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
    }

    @Override
    protected ValidationSchemaSyntaxErrorExceptionMapper createMapper() {
        return new ValidationSchemaSyntaxErrorExceptionMapper();
    }
}
