(ns async-crawler.core
  (:use clojure.tools.logging
        clojure.stacktrace)
  (:require [clojure.set :as cljset]
            [clojure.core.reducers :as r]
            [async-crawler.toile.spider :as spider]
            [clojure.core.async
             :as async
             :refer [chan go >! go-loop <! <!! timeout]]
            [async-crawler.data.crawler :as crwl])
  (:gen-class))

(defn crawl [new_url]
  (go
    (<! (timeout 4000))
    (let [urls_chan (chan)
          process_url (comp
                        spider/urls
                        <!!
                        spider/fetch)
          collected_urls (process_url new_url)]
      (debug "Collected Urls: " collected_urls)
      (crwl/store "visited_urls" urls_chan collected_urls)
      (go-loop []
               (when-let [url (<! urls_chan)]
                 (crawl url)
                 (recur))))
    ))

(defn seed [urls]
  (doseq [url urls]
    (go
      (crawl url))))

(comment
  (seed [
          "http://www.imdb.com/"
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
          ])
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
