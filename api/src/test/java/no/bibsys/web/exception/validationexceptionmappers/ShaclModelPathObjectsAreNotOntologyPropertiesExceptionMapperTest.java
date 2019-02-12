package no.bibsys.web.exception.validationexceptionmappers;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import no.bibsys.entitydata.validation.exceptions.ShaclModelPathObjectsAreNotOntologyPropertiesException;
import no.bibsys.web.exception.MapperTest;
import no.bibsys.web.model.RegistryDto;

public class ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapperTest
        extends MapperTest<ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper> {

    @Test 
    public void updateRegistrySchema_invalidSchemaNotOntologyProperies_ReturnsStatusBadRequest() throws Exception {
        
        when(mockRegistryService.updateRegistrySchema(anyString(), anyString())).thenAnswer(invocation -> { 
            throw new ShaclModelPathObjectsAreNotOntologyPropertiesException();
        });
        
        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        Response response = putSchema(registryName, validValidationSchema);

        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));

    }

    @Override
    protected ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper createMapper() {
        return new ShaclModelPathObjectsAreNotOntologyPropertiesExceptionMapper();
    }
}
