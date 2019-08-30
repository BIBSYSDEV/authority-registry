package no.bibsys.service;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import no.bibsys.aws.tools.Environment;

public class SearchServiceTest {

    @Ignore
    @Test
    public void test() {
        String registryName = "humord";
        String queryString = "ordbok*";
        SearchService searchService = new SearchService(new Environment());
        String result = searchService.simpleQuery(registryName, queryString);
        assertNotNull(result);
        System.out.println(result);

    }

}
