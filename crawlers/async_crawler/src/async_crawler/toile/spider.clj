(ns async-crawler.toile.spider
  (:use clojure.pprint
        clojure.tools.logging)
  (:require [clojure.core.async :as async :refer [>! <! <!! alts!! close! chan sliding-buffer go]]
            [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn fetch [url]
  (let [response (client/get url {:as :stream})
        {headers :headers
         body :body
         status :status} response
        host (cond
               (.endsWith url "/") url
               :else url)]
    (debug "Fetched: " url)
    {:url host
     :headers headers
     :body (html/html-resource body)
     :status status}))

(defn urls [pdata]
  (let [url (:url pdata)
        hrefs (pmap
                #(-> % :attrs :href)
                (html/select (:body pdata) #{[:a]}))
        not-nil-hrefs (remove nil? hrefs)
        no-frag-hrefs (remove #(.startsWith % "#") not-nil-hrefs)
        urls (->> no-frag-hrefs
                  (pmap #(cond
                          (.startsWith % "/") (str url %)
                          :else %))
                  (pmap #(cond
                          (not
                            (.startsWith % "http")) (str url "/" %)
                          :else %)))]
    (debug (format "Retrieved %d urls from %s" (count urls) url))
    urls))





