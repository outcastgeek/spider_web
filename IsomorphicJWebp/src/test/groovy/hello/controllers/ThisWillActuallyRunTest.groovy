package hello.controllers

import spock.lang.Specification

/**
 * Created by bebby on 3/4/2015.
 */
class ThisWillActuallyRunTest extends Specification {

    ThisWillActuallyRun thisWillActuallyRun

    void setup() {
        thisWillActuallyRun = new ThisWillActuallyRun()
    }

    void cleanup() {

    }

    def "Home"() {
        expect:
        assert thisWillActuallyRun.home() == "Hello Groovy World!!!!"
    }
}
