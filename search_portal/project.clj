(defproject search_portal "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]

                 [io.pedestal/pedestal.service "0.4.0"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Tomcat or Immutant instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.4.0"]
                 ;; [io.pedestal/pedestal.tomcat "0.4.0"]
                 ;;[io.pedestal/pedestal.immutant "0.4.0"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.12"]
                 [org.slf4j/jcl-over-slf4j "1.7.12"]
                 [org.slf4j/log4j-over-slf4j "1.7.12"]

                 [clj-http "2.0.0"]
                 [ns-tracker "0.3.0"]
                 ;; Database
                 [yesql "0.5.0"]
                 [mysql/mysql-connector-java "5.1.32"]
                 ;; Caching
                 [org.clojure/core.cache "0.6.4"]
                 ;; Django templates for Clojure
                 [selmer "0.9.0"]
                 [markdown-clj "0.9.69"]
                 ;; Security
                 [buddy "0.6.2"]
                 [bouncer "0.3.3"]
                 ;;[com.cemerick/friend "0.2.1"]
                 ]
  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-figwheel "0.3.7"]
            [lein-ring "0.9.6"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :ring {:handler search-portal.service/routes}
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "search-portal.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.4.0"]
                                  [org.xerial/sqlite-jdbc "3.7.2"]
                                  ;;; War Deployment
                                  ;[javax.servlet/servlet-api "2.5"]
                                  ;; Clojurescript
                                  [org.clojure/clojurescript "1.7.48"]
                                  [org.omcljs/om "0.9.0" :exclusions [cljsjs/react]]
                                  [cljsjs/react-with-addons "0.13.3-0"]
                                  [sablono "0.3.5" :exclusions [cljsjs/react]]
                                  [prismatic/om-tools "0.3.12"]
                                  [cljs-http "0.1.37"]
                                  [figwheel "0.3.7"]]}
             :prod {:aot :all}}
  :main ^{:skip-aot true} search-portal.server)

