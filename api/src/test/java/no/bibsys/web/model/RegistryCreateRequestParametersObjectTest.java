package no.bibsys.web.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class RegistryCreateRequestParametersObjectTest {

    private static final String PATH = "path";
    private static final String IDENTIFIER = "identifier";
    private static final String APIKEY = "apikey";
    private static final String METADATA_VALUE = "dataValue";
    private static final String METADATA_KEY = "data";
    private Map<String, Object> metadataMap = new ConcurrentHashMap<>();

    private static final String DIFFERENT_API_KEY = "differentApiKey";
    private static final String DIFFERENT_PATH = "differentPath";

    @Test
    public void toRegistryDto_parameterObjectWithData_properRegistryDto() {
        RegistryCreateRequestParametersObject createObject = createParameterObject();

        RegistryDto registryDto = createObject.toRegistryDto();
        assertThat(registryDto.getApiKey(), is(createObject.getApiKey()));
        assertThat(registryDto.getApiKey(), is(APIKEY));
        assertThat(registryDto.getId(), is(createObject.getId()));
        assertThat(registryDto.getId(), is(IDENTIFIER));
        assertThat(registryDto.getPath(), is(createObject.getPath()));
        assertThat(registryDto.getPath(), is(PATH));
        assertThat(registryDto.getMetadata(), is(createObject.getMetadata()));
        assertThat(registryDto.getMetadata(), is(metadataMap));
    }

    @Test
    public void equals_twoParameterObjectsWithSameData_true() {
        RegistryCreateRequestParametersObject objectOne = createParameterObject();
        
        RegistryCreateRequestParametersObject objectTwo = createParameterObject();
        
        assertThat(objectOne, is(equalTo(objectOne)));
        assertThat(objectOne, is(not(equalTo(new RegistryCreateRequestParametersObject()))));
        assertThat(objectOne, is(equalTo(objectTwo)));
        assertThat(objectOne, is(not(equalTo(new RegistryDto()))));
        
        objectTwo.setApiKey(DIFFERENT_API_KEY);
        assertThat(objectOne, is(not(equalTo(objectTwo))));
        
        objectTwo.setApiKey(APIKEY);
        assertThat(objectOne, is(equalTo(objectTwo)));
        objectTwo.setPath(DIFFERENT_PATH);
        assertThat(objectOne, is(not(equalTo(objectTwo))));
    }

    @Test
    public void toStringHashcode_parameterObject_noException() {
        RegistryCreateRequestParametersObject parameterObject = createParameterObject();
        assertThat(parameterObject.toString(), is(not("")));
        assertThat(parameterObject.hashCode(), is(not(0)));
    }

    private RegistryCreateRequestParametersObject createParameterObject() {
        
        RegistryCreateRequestParametersObject parameterObject = new RegistryCreateRequestParametersObject();
        
        metadataMap.put(METADATA_KEY, METADATA_VALUE);
        parameterObject.setApiKey(APIKEY);
        parameterObject.setId(IDENTIFIER);
        parameterObject.setMetadata(metadataMap);
        parameterObject.setPath(PATH);
        
        return parameterObject;
    }
}
