(ns async-crawler.toile.spider
  (:use clojure.pprint
        clojure.tools.logging)
  (:require [clojure.core.async :as async :refer [>! <! <!! alts!! close! chan sliding-buffer go]]
            [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn grab [url]
  (let [response (client/get url {:as :stream})
        {headers :headers
        body :body
        status :status} response
        content (html/html-resource body)
        title (html/text
                (first (html/select content [[:title]])))
        ]
    (debug "Retrieved page with Title: " title)
    {:headers headers
     :title title
     :body content
     :status status}))



