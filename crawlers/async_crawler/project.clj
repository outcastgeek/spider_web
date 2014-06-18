(defproject async_crawler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [org.zeromq/jeromq "0.3.4"]
                 [org.clojure/tools.logging "0.3.0"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [org.jsoup/jsoup "1.7.3"]]
  :main ^:skip-aot async-crawler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
