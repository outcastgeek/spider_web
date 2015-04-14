package hello.controllers

import groovy.transform.CompileStatic
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@CompileStatic
class ThisWillActuallyRun {

    @RequestMapping("/gr8")
    String home() {
        return "Hello Groovy World!!!!"
    }

    @RequestMapping("/thymeleaf")
    ModelAndView someThyme() {
//        ModelAndView modelView = new ModelAndView("gr8/gr8", "message", "Some Veggies!!!!")
        ModelAndView modelView = new ModelAndView("gr8/thyme", "message", "Some Veggies!!!!")
        return modelView
    }
}





