package no.bibsys.testtemplates;

import no.bibsys.LocalDynamoConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@ContextConfiguration(classes = {LocalDynamoConfiguration.class})
public abstract class LocalDynamoTest extends SampleData {

}
