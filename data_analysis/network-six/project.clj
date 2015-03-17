(defproject network-six "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "1.0.5"]]
  :aot :all
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [me.raynes/fs "1.4.6"]
                 ;; Clojurescript
                 [org.clojure/clojurescript "0.0-3123"]]
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:pretty-printer true
                                   :output-to "www/js/main.js"
                                   :optimizations :whitespace}}]})
