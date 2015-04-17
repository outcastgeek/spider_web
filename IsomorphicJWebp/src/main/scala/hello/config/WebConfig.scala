package hello.config

import javax.script.ScriptEngineManager

import hello.utils.JsUtil
import jdk.nashorn.api.scripting.NashornScriptEngine
import org.springframework.context.annotation.{Bean, Configuration, Scope}
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, ResourceHandlerRegistry, WebMvcConfigurerAdapter}

/**
 * Created by bebby on 4/16/2015.
 */
@Configuration
@EnableWebMvc
class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  @Scope(value = "prototype")
  def nashorn = {
    val nashorn = new ScriptEngineManager().getEngineByName("nashorn").asInstanceOf[NashornScriptEngine]

    //    nashorn.eval(JsUtil.read("static/vendor/jquery.js"))
    //    nashorn.eval(JsUtil.read("static/vendor/bootstrap.js"))
    //    nashorn.eval(JsUtil.read("static/vendor/react-with-addons.js"))
    //    nashorn.eval(JsUtil.read("static/vendor/JSXTransformer.js"))

    //    nashorn.eval(JsUtil.read("static/vendor/jquery.min.js"))
    //    nashorn.eval(JsUtil.read("static/vendor/bootstrap.min.js"))
    nashorn.eval(JsUtil.read("static/vendor/react-with-addons.min.js"))
    nashorn.eval(JsUtil.read("static/vendor/JSXTransformer.js"))

    //    nashorn.eval(JsUtil.read("webjars/jquery/2.1.3/dist/js/jquery.min.js"))
    //    nashorn.eval(JsUtil.read("webjars/bootstrap/3.3.4/dist/js/bootstrap.min.js"))
    //    nashorn.eval(JsUtil.read("webjars/react/0.13.1/react-with-addons.min.js"))
    //    nashorn.eval(JsUtil.read("webjars/react/0.13.1/JSXTransformer.js"))

    nashorn
  }

  override def addResourceHandlers(registry: ResourceHandlerRegistry) = {

    if (!registry.hasMappingForPattern("/webjars/**")) {
      registry.addResourceHandler("/webjars/**").addResourceLocations(
        "classpath:/META-INF/resources/webjars/"
      )
    }
    if (!registry.hasMappingForPattern("/static/**")) {
      registry.addResourceHandler("/static/**").addResourceLocations(
        "classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/resources/static/",
        "classpath:/static/", "classpath:/public/"
      )
    }
  }
}
