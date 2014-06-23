(ns async-crawler.toile.spider
  (:use clojure.pprint
        clojure.tools.logging)
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn- is_url? [href]
  (let [url_match (re-find
                    (re-pattern "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
                    href)]
    (nil? url_match)))

(defn fetch [url]
  (let [response (try
                   (client/get url {:as :stream})
                   (catch Exception e
                     (error "ERROR::::" e)
                     {}))
        {headers :headers
         body :body
         status :status} response
        address (cond
                  (.endsWith url "/")
                    (. url substring 0 (- (count url) 1))
                  :else url)]
    ;(debug "Fetched: " address)
    {:url address
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
        urls (pmap #(cond
                     (.startsWith % "/") (str url %)
                     :else (str url "/" %)) no-frag-hrefs)
        ]
    ;(debug (format "Retrieved %d urls from %s" (count urls) url))
    urls))





