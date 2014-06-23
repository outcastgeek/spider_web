(ns async-crawler.core
  (:use clojure.tools.logging
        clojure.stacktrace)
  (:require [clojure.set :as cljset]
            [clojure.core.reducers :as r]
            [async-crawler.toile.spider :as spider]
            [clojure.core.async
             :as async
             :refer [>! <! >!! <!! alts!! close! chan sliding-buffer go go-loop onto-chan timeout]])
  (:gen-class))

(def visited_urls
  (agent
    (set [])
    :validator #(and
                 (every? (comp not nil?) %)
                 (every? string? %))
    :error-handler #(error
                     (format "Whoops!! << %s >> had a problem: << %s >>\nCurrent State: << %s >>"
                             %1 (agent-error %2) @%1))
    ))

(defn crawl [idf agnt old_urls new_urls]
  ;(debug
  ;  (format "%s ==>> %s" agnt idf))
  (go
    (let [urls_to_crawl (cljset/difference new_urls old_urls)
          collected_urls (flatten
                           (pmap (comp
                                  spider/urls
                                  spider/fetch)
                                urls_to_crawl))]
      (send visited_urls cljset/union (set collected_urls)))
    ))

(add-watch visited_urls :crawl crawl)

(defn seed [urls]
  (send visited_urls cljset/union (set urls)))

(comment
  (seed [
          "http://allafrica.com/cotedivoire/"
          "http://outcastgeek.com/"
          "http://vieupai.com/"
          "http://www.ivorian.net/"
          "http://abidjandirect.net/"
          "http://abidjanshow.com/v2x/home/"
          "http://abidjan.net/"
          "http://www.travelportland.com/"
          "http://www.thisisyourkingdom.co.uk/category/places-to-eat-and-drink/"
          "http://worryfreelabs.com/"
          "https://en.wikipedia.org/wiki/Ivory_Coast"
          "http://www.fifa.com/"
          "http://www.fifa.com/worldcup/news/"
          "https://www.yahoo.com/"
          ]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
