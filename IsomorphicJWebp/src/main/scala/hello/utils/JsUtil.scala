package hello.utils

import java.io.InputStreamReader

/**
 * Created by bebby on 4/16/2015.
 */
object JsUtil {
  def read(path: String) = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(path)
    val inputStreamReader = new InputStreamReader(inputStream)
    inputStreamReader
  }
}
