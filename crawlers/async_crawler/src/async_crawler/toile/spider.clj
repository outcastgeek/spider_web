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
         status :status} response]
    (debug "Fetched: " url)
    {:headers headers
     :body (html/html-resource body)
     :status status}))




