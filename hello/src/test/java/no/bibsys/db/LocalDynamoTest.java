package no.bibsys.db;

import no.bibsys.LocalDynamoConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@ContextConfiguration(classes = {LocalDynamoConfiguration.class})
@RunWith(SpringRunner.class)
@DirtiesContext
public abstract class LocalDynamoTest {

}
