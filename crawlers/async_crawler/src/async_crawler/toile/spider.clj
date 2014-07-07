(ns async-crawler.toile.spider
  (:use clojure.pprint
        clojure.tools.logging
        [clojure.java.io :only [input-stream]])
  (:require [org.httpkit.client :as client]
            [net.cgrand.enlive-html :as html]
            [clojure.core.async
             :as async
             :refer [chan go put! >! <! <!! timeout]])
  (:import (java.io ByteArrayInputStream)
           (org.apache.tika.sax BodyContentHandler)
           (org.apache.tika.metadata Metadata)
           (org.apache.tika.parser ParseContext)
           (org.apache.tika.parser.html HtmlParser)))

(defn- is_url? [href]
  (let [url_match (re-find
                    (re-pattern "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
                    href)]
    (nil? url_match)))

(defn throw-err [e]
  (when (instance? Throwable e) (throw e))
  e)

(defmacro <? [ch]
  `(throw-err (async/<! ~ch)))

(defn- http-get [url]
  (let [req-chan (chan)]
    (client/get url
                {:as :byte-array
                 :timeout 60000}
                (fn [resp]
                  (put! req-chan resp)))
    req-chan))

(defn fetch [url]
  (debug "Fetching: " url)
  (let [data-chan (chan)]
    (go
      (try
        (let [response (<? (http-get url))
              {headers :headers
               body :body
               status :status} response
              address (cond
                        (.endsWith url "/")
                        (. url substring 0 (- (count url) 1))
                        :else url)]
          (>! data-chan {:url address
                         :headers headers
                         :body (html/html-resource
                                 (input-stream body))
                         :stream (input-stream body)
                         :status status}))
        (catch Exception e
               (>! data-chan {:url url :body ""})
               (error "ERROR::::" e))))
    data-chan))

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
    (debug (format "Retrieved %d urls from %s" (count urls) url))
    urls))

;; Borrowed from here: https://github.com/dakrone/itsy/blob/master/src/itsy/extract.clj
(defn html->str
  "Convert HTML to plain text using Apache Tika"
  [pdata]
  (let [stream (:stream pdata)
        handler (BodyContentHandler.)
        metadata (Metadata.)
        parser (HtmlParser.)]
    (.parse parser stream handler metadata (ParseContext.))
    (.toString handler)))






