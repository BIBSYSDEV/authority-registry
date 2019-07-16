package no.bibsys.web.exception.validationexceptionmappers;

import no.bibsys.entitydata.validation.exceptions.EntityFailedShaclValidationException;
import no.bibsys.web.exception.MapperTest;
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

public class EntityFailedShaclValidationExceptionMapperTest
        extends MapperTest<EntityFailedShaclValidationExceptionMapper> {

    private static final String SOME_TURTLE = "Some TURTLE";

    @Test
    public void insertEntity_entityFailedValidation_ReturnsStatusBadRequest() throws Exception {

        when(mockEntityService.addEntity(anyString(), any())).thenAnswer(invocation -> { 
            throw new EntityFailedShaclValidationException(SOME_TURTLE);
        });

        String registryName = UUID.randomUUID().toString();
        RegistryDto registryDto = sampleData.sampleRegistryDto(registryName);
        createRegistry(registryDto, apiAdminKey);
        putSchema(registryName, validValidationSchema);

        EntityDto expectedEntity = sampleData.sampleEntityDto();
        Response response = insertEntityRequest(registryName, expectedEntity, apiAdminKey);
        assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
    }

    @Override
    protected EntityFailedShaclValidationExceptionMapper createMapper() {
        return new EntityFailedShaclValidationExceptionMapper();
    }
}
