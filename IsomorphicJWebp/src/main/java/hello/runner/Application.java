package hello.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by bebby on 2/26/2015.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("hello")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
