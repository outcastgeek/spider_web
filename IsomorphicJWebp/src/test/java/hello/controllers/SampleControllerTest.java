package hello.controllers;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import hello.runner.Application;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class SampleControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Ignore
    @Test
    public void testHome() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate()
                .getForEntity("http://localhost:" + this.port, String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Hello World!".equals(entity.getBody()));
    }
}
