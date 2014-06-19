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
          [title other] (map
                          html/text
                          (-> body
                              (html/select #{[:title]
                                             [:#Other_areas_of_Wikipedia]}))
                          )
          ]
      (debug "Retrieved page with Title: " title)
      (debug "#Other_areas_of_Wikipedia: " other)
      (is (= status
             200))
      (is (= title
             "Wikipedia, the free encyclopedia"))
      (is (= other
             "Other areas of Wikipedia"))
      )))

