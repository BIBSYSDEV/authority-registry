package no.bibys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** Simple class to start up the application.
 *
 * @SpringBootApplication adds:
 *  @Configuration
 *  @EnableAutoConfiguration
 *  @ComponentScan
 */
@SpringBootApplication
@ComponentScan("no.bibsys")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



}
