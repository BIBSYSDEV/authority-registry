package no.bibsys.testtemplates;


import no.bibsys.LocalDynamoConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest
@DirtiesContext
@ContextConfiguration(classes = {LocalDynamoConfiguration.class})
public abstract class ApiTest extends SampleData {

}
