package hello.controllers

import akka.pattern.ask
import akka.util.Timeout
import hello.actors.Messages._
import hello.actors.NashornActor.RenderComponent
import hello.actors.{ActorFactory, CommentActor, ThingActor}
import hello.models.{Comment, Thing}
import hello.utils.aop.annotations.Loggable
import hello.utils.aop.interceptors.LoggingInterceptor
import hello.utils.aop.{ManagedComponentFactory, ManagedComponentProxy}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, ResponseBody}
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView

import scala.collection.JavaConverters._
import scala.collection.immutable.{List, Map}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait Foo {
  def foo(msg: String)
}

class FooImpl extends Foo {
  @Loggable
  def foo(msg: String) = println("msg: " + msg)
}

/**
 * Created by bebby on 3/25/2015.
 */
@Controller
class ScalaController @Autowired()
(workerPool:ExecutionContext, actorFactory: ActorFactory) {

  val LOG = LoggerFactory.getLogger(classOf[ScalaController])

  var foo = ManagedComponentFactory.createComponent[Foo](
    classOf[Foo],
    new ManagedComponentProxy(new FooImpl)
      with LoggingInterceptor)

  implicit val timeout = Timeout(4 seconds)
  implicit val ec = workerPool
  implicit val af = actorFactory

  val (weather, geoIp, thingCrud, commentCrud, crawler, nashorn) = (
    actorFactory.genWeatherActor,
    actorFactory.genGeoIpActor,
    actorFactory.genThingCrudActor,
    actorFactory.genCommentCrudActor,
    actorFactory.genCrawlActor,
    actorFactory.genNashornActor
    )

  @RequestMapping(Array("/_ah/start"))
  @ResponseBody
  def start = "Application Started"

  @RequestMapping(Array("/_ah/health"))
  @ResponseBody
  def health = "Healthy Application"

  @RequestMapping(Array("/aop"))
  @ResponseBody
  def scalaAop: ModelAndView = {
    val aopMsg = "Hello AOP"
    foo.foo(aopMsg)
    new ModelAndView("gr8/thyme", "message", aopMsg)
  }

  @RequestMapping(Array("/makeNewThing/{name:.+}"))
  @ResponseBody
  def makeNewThing(@PathVariable("name") name: String): DeferredResult[ModelAndView] = {

    val thing = new Thing()
    thing.name = name

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("thing/detail")

    implicit val delay: FiniteDuration = 4 seconds

    thingCrud ? ThingActor.Save(thing) andThen {
      case Success(data) =>
        mv.addAllObjects(Map("thing_data" -> data).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/allThingsNamed/{name:.+}"))
  @ResponseBody
  def allThingsNamed(@PathVariable("name") name: String): DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("thing/list")

    implicit val delay: FiniteDuration = 4 seconds

    thingCrud ? ThingActor.ByName(name) andThen {
      case Success(data) =>
        mv.addAllObjects(Map("thing_data" -> data).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/getAllThings"))
  @ResponseBody
  def getThingByName: DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("thing/list")

    implicit val delay: FiniteDuration = 4 seconds

    thingCrud ? All andThen {
      case Success(data) =>
        mv.addAllObjects(Map("thing_data" -> data).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/scala"))
  @ResponseBody
  def home: ModelAndView = {
    new ModelAndView("gr8/thyme", "message", "Hello Scalable World!!!!")
  }

  @RequestMapping(Array("/comments"))
  @ResponseBody
  def getComments: DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("comments")

    implicit val delay: FiniteDuration = 4 seconds

    commentCrud ? All andThen {
      case Success(comments) =>
        nashorn ? RenderComponent(
          comments.asInstanceOf[Object],
          List(
            "static/vendor/showdown.min.js",
            "static/commentBox.js"
          )
        ) andThen {
          case Success(rendered_html) =>
            mv.addAllObjects(Map("rendered_html" -> rendered_html).asJava)
            response.setResult(mv)
          case Failure(_) =>
            LOG.error("ERROR::::")
        }
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(value = Array("/comments.json"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  @ResponseBody
  def commentsList():DeferredResult[java.util.List[Comment]] = {

    implicit val response = new DeferredResult[java.util.List[Comment]]()

    implicit val delay: FiniteDuration = 4 seconds

    commentCrud ? All andThen {
      case Success(comments) =>
        response.setResult(comments.asInstanceOf[java.util.List[Comment]])
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(value = Array("/comments.json"), method = Array(RequestMethod.POST), produces = Array("application/json"))
  @ResponseBody
  def addComments(comment: Comment):DeferredResult[Comment] = {

//    var count = 0
//    for (count <- 1 to 4) {
//      val comment = new Comment()
//      comment.author = s"author#$count"
//      comment.text = s"Comment#$count"
//      commentRepository.save(comment)
//    }

    implicit val response = new DeferredResult[Comment]()

    implicit val delay: FiniteDuration = 4 seconds

    commentCrud ? CommentActor.Save(comment) andThen {
      case Success(comment) =>
        response.setResult(comment.asInstanceOf[Comment])
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/crawl/{ip_or_hostname:.+}"))
  @ResponseBody
  def crawlLocation(@PathVariable("ip_or_hostname") ip_or_hostname: String): DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("crawl/list")

    implicit val delay: FiniteDuration = 4 seconds

    crawler ? Get(ip_or_hostname) andThen {
      case Success(data) =>
        mv.addAllObjects(Map("crawl_data" -> data).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/geolocation/{ip_or_hostname:.+}"))
  @ResponseBody
  def getGeolocation(@PathVariable("ip_or_hostname") ip_or_hostname: String): DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("geolocation")

    implicit val delay: FiniteDuration = 4 seconds

    geoIp ? Get(ip_or_hostname) andThen {
      case Success(data) =>
        mv.addAllObjects(
          Map(
          "ip_or_hostname" -> ip_or_hostname,
          "geo_location" -> data
          ).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

  @RequestMapping(Array("/weather/{station}"))
  @ResponseBody
  def getTemperature(@PathVariable("station") station: String): DeferredResult[ModelAndView] = {

    implicit val response = new DeferredResult[ModelAndView]()

    implicit val mv = new ModelAndView("weather")

    implicit val delay: FiniteDuration = 4 seconds

    weather ? Get(station) andThen {
      case Success(data) =>
        mv.addAllObjects(Map("weather" -> data).asJava)
        response.setResult(mv)
      case Failure(_) =>
        LOG.error("ERROR::::")
    }

    response
  }

//  @RequestMapping(Array("/weather/{station}"))
//  @ResponseBody
//  def getTemperature(@PathVariable("station") station: String): DeferredResult[ModelAndView] = {
//    val response = new DeferredResult[ModelAndView]()
//    val result = for {
//      currentWeather <- ask(
//        weather,
//        CurrentWeather(station, s"http://w1.weather.gov/xml/current_obs/${station.toUpperCase()}.xml"),
//        timeout).mapTo[String]
//    } yield currentWeather
//    result.foreach(currentWeather =>
//      response.setResult(new ModelAndView("gr8/thyme", "message", currentWeather))
//    )
//    response
//  }

//  @RequestMapping(Array("/weather/{station}"))
//  @ResponseBody
//  def getTemperature(@PathVariable("station") station: String): DeferredResult[ModelAndView] = {
//    val currentWeather = Await.result((weather ? CurrentWeather(
//      station, s"http://w1.weather.gov/xml/current_obs/${station.toUpperCase()}.xml")),
//      timeout.duration).asInstanceOf[String]
//    val res = new DeferredResult[ModelAndView]()
//    res.setResult(new ModelAndView("gr8/thyme", "message", currentWeather))
//    res
//  }

//  @RequestMapping(Array("/sparkJob/{numberOfSamples}  "))
//  @ResponseB  ody
//  def sparkJob(@PathVariable(value = "numberOfSamples") numberOfSamples: Int) : ModelAndView   = {
//    val conf = new SparkConf().setAppName("PI Approximato  r")
//    val sCtx = new SparkContext(co  nf)
//    val count = sCtx.parallelize(1 to numberOfSamples).map {i   =>
//      val x = Math.rando  m()
//      val y = Math.rando  m()
//      if (x*x + y*y < 1) 1 els  e 0
//    }.reduce(_ +   _)
//    val piVal = 4.0 * count / numberOfSamp  les
//    new ModelAndView("gr8/thyme", "message", s"The approximate value of PI from $numberOfSamples samples is $piVa  l")
//  }
}

