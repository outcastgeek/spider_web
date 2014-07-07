(ns async-crawler.data.crawler
  (:use clojure.tools.logging)
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clojure.core.async
             :as async
             :refer [chan go >! >!! go-loop <! <!! to-chan]]))

(def server-conn {:pool {:max-active 8}
                  :spec {:host "localhost"
                         :port 6379
                         :timeout 4000}})

(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn store [coll_name callback_chan coll]
  (let [not_included? (fn [itm]
                        (= 0 (wcar*
                               (car/sismember coll_name itm))))
        filter_out_existing (fn [item]
                              (when
                                  (not_included? item)
                                (debug "Does not include: " item)
                                item))
        new_items_chan (->> (to-chan coll)
                            (async/map< filter_out_existing)
                            (async/remove< nil?))]
    (go-loop []
             (when-let [new_item (<! new_items_chan)]
               (debug "Publishing: " new_item)
               (go
                 (>! callback_chan new_item))
               (go
                 (wcar*
                   (car/sadd coll_name new_item)))
               (recur)))
    ))





