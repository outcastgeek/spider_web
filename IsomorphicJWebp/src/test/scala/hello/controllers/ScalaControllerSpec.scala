package hello.controllers

import hello.runner.Application
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.{IntegrationTest, SpringApplicationConfiguration, TestRestTemplate}
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

/**
 * Created by bebby on 4/7/2015.
 */
@RunWith(value = classOf[SpringJUnit4ClassRunner])
@SpringApplicationConfiguration(classes = Array(classOf[Application]))
@WebAppConfiguration
@IntegrationTest(value = Array("server.port:0"))
@DirtiesContext
class ScalaControllerSpec extends JUnitSuite {

  @Value(value = "${local.server.port}")
  var port: Int = _

  @Test def testHome() {
    def entity = new TestRestTemplate().getForEntity(s"http://localhost:$port/scala", classOf[String])
    assert(HttpStatus.OK == entity.getStatusCode)
    assert(entity.getBody.contains("Hello Scalable World!!!!"))
  }

  @Test def testStart() {
    def entity = new TestRestTemplate().getForEntity(s"http://localhost:$port/_ah/start", classOf[String])
    assert(HttpStatus.OK == entity.getStatusCode)
    assert(entity.getBody.contains("Application Started"))
  }

  @Test def testHealth() {
    def entity = new TestRestTemplate().getForEntity(s"http://localhost:$port/_ah/health", classOf[String])
    assert(HttpStatus.OK == entity.getStatusCode)
    assert(entity.getBody.contains("Healthy Application"))
  }
}
