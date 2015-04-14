package hello.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * Created by bebby on 4/8/2015.
 */
class ScalaObjectMapper extends ObjectMapper {

  registerModule(DefaultScalaModule)
}
