(ns async-crawler.toile.spider-test
  (:use clojure.test
        clojure.tools.logging
        async-crawler.toile.spider)
  (:require [net.cgrand.enlive-html :as html]
            [clojure.core.async
             :as async
             :refer [<!!]]))

(deftest test-grab
  (testing "Fetching"
    (let [content (<!! (fetch "http://en.wikipedia.org"))
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
      ))
  (testing "Collecting"
    (let [content (<!! (fetch "http://en.wikipedia.org"))
          collected_urls (urls content)
          text (html->str content)]
      (debug text)
      (is (= (first collected_urls)
             "http://en.wikipedia.org/wiki/Wikipedia")
          (= (second collected_urls)
             "http://en.wikipedia.org/wiki/Free_content")))))

