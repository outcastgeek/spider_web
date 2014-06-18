(ns async-crawler.toile.spider-test
  (:use clojure.test
        clojure.tools.logging
        async-crawler.toile.spider)
  (:require [net.cgrand.enlive-html :as html]))

(deftest test-grab
  (testing "Grabbing"
    (let [content (fetch "http://en.wikipedia.org")
          {status :status
           body :body} content
          title-func (fn [content]
                       (-> content
                           (html/select [[:title]])
                           first
                           html/text))
          title (select body title-func)
          ]
      (debug "Retrieved page with Title: " title)
      (is (= status
             200))
      (is (= title
             "Wikipedia, the free encyclopedia"))
      )))

