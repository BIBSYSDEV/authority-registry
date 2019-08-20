package no.bibsys.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.cloudsearchdomain.model.Hit;
import com.amazonaws.services.cloudsearchdomain.model.Hits;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.bibsys.utils.IoUtils;
import no.bibsys.utils.JsonUtils;

public class SearchServiceTest {

    private static final String TESTDATA_FOLDER = "testdata";
    private static final String bodyFilename = "cloudsearch_response.json";
    private static final Object CLOUDSEARCH_RETURN_FIELD = "presentation_json";
    private final transient ObjectMapper objectMapper = JsonUtils.newJsonParser();

    @Ignore
    @Test
    public void testSimpleQuery() throws IOException {
        String body = IoUtils.resourceAsString(Paths.get(TESTDATA_FOLDER, bodyFilename));
        SearchResult searchResult = objectMapper.readValue(body, SearchResult.class);
        assertNotNull(searchResult);
        Hits hits = searchResult.getHits();
        for (Hit hit : hits.getHit()) {
            List<String> list = hit.getFields().get(CLOUDSEARCH_RETURN_FIELD);
            for (String presentationJsonString : list) {
                assertNotNull(presentationJsonString);
            }
        }
    }

}
