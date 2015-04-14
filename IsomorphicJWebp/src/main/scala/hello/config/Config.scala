package hello.config

import akka.actor.ActorSystem
import hello.utils.ScalaObjectMapper
import hello.utils.SpringExtension.SpringExtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext

/**
 * Created by bebby on 4/8/2015.
 */
@Configuration
@EnableJpaRepositories(basePackages = Array("hello.models.repositories"))
@EntityScan(basePackages = Array("hello.models"))
class Config {

  @Autowired
  var applicationContext: ApplicationContext = _

  @Bean
  @Primary
  def scalaObjectMapper = new ScalaObjectMapper()

  @Bean(name = Array("workerPool"))
  def executionContext = {
    val workerPool = ExecutionContext.fromExecutor(new scala.concurrent.forkjoin.ForkJoinPool)
    workerPool
  }

  @Bean
  def restTemplate = {
    val restTemplate = new RestTemplate()
    restTemplate getMessageConverters() foreach {
      case mc: MappingJackson2HttpMessageConverter =>
        mc.setObjectMapper(scalaObjectMapper)
      case _ =>
    }
    restTemplate
  }

  @Bean
  def actorSystem = {
    val system = ActorSystem.create("IsomorphicJWebp")
    // Initialize the application context in the Akka Spring Extension
    SpringExtProvider.get(system).initialize(applicationContext)
    system
  }

//  @Bean
//  def entityManagerFactory = Persistence.createEntityManagerFactory("default")
//
//  @Bean
//  def hibernateExceptionTranslator = new HibernateExceptionTranslator
//
//  @Bean
//  def transactionManager = new JpaTransactionManager(entityManagerFactory)
//
//  @Bean
//  def jpaRepositoryFactory:RepositoryFactorySupport = new JpaRepositoryFactory(entityManagerFactory.createEntityManager())
//
//  @Bean
//  def thingRepository = jpaRepositoryFactory.getRepository(classOf[ThingRepository])

//  @Bean
//  def embeddedServletContainerFactory: UndertowEmbeddedServletContainerFactory = {
//    val numCPUs = Runtime.getRuntime.availableProcessors
//    val factory = new UndertowEmbeddedServletContainerFactory;
//    factory.addBuilderCustomizers(new UndertowBuilderCustomizer {
//      override def customize(builder: Builder): Unit = {
//        builder.setIoThreads(numCPUs)
//        builder.setWorkerThreads(numCPUs)
//      }
//    });
//    return factory;
//  }
}
