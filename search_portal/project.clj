(defproject search_portal "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.pedestal/pedestal.service "0.3.1"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Tomcat or Immutant instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.3.1"]
                 ;; [io.pedestal/pedestal.tomcat "0.3.1"]
                 ;;[io.pedestal/pedestal.immutant "0.3.1"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]
                 ;; Django templates for Clojure
                 [selmer "0.8.2"]
                 ;; Clojurescript
                 [org.clojure/clojurescript "0.0-2760"]
                 [org.omcljs/om "0.8.8" :exclusions [cljsjs/react]]
                 [cljsjs/react-with-addons "0.12.2-4"]
                 ;; Security
                 ;;[com.cemerick/friend "0.2.1"]
                 ]
  :plugins [[lein-cljsbuild "1.0.5"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "search-portal.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.3.1"]]}
             :prod {:aot :all}}
  :main ^{:skip-aot true} search-portal.server)
