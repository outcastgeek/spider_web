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
                 [http-kit "2.1.18"]
                 [enlive "1.1.5"]
                 [org.apache.tika/tika-core "1.5"]
                 [org.apache.tika/tika-parsers "1.5"]
                 [com.taoensso/carmine "2.6.2"]]
  :main ^:skip-aot async-crawler.core
  :aot :all
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
