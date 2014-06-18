(ns async-crawler.toile.spider-test
  (:use clojure.test
        clojure.tools.logging
        async-crawler.toile.spider))

(deftest test-grab
  (testing "Grabbing"
    (let [content (grab "http://en.wikipedia.org")
          {status :status
           title :title} content]
      (is (= status
             200))
      (is (= title
             "Wikipedia, the free encyclopedia"))
      )))

