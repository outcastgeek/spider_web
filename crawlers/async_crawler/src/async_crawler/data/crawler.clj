(ns async-crawler.data.crawler
  (:use clojure.tools.logging)
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clojure.core.async
             :as async
             :refer [chan go >! >!! go-loop <! <!! to-chan timeout]]))

(def server-conn {:pool {:max-active 8}
                  :spec {:host "localhost"
                         :port 6379
                         :timeout 4000}})

(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(def coll_name "visited_urls")

(defn- not_included? [itm]
  (= 0 (wcar*
         (car/sismember coll_name itm))))

(defn- filter_out_existing [item]
  (when
    (not_included? item)
    (debug "Does not include: " item)
    item))

(defn store [out_chan in_chan]
  (go-loop []
    (when-let [new_item (<! in_chan)]
      (debug "Publishing: " new_item)
      (go
        (<! (timeout (rand-int 30000)))
        (>! out_chan new_item))
      (go
        (wcar*
          (car/sadd coll_name new_item)))
      (recur))))

;(defn store [callback_chan coll]
;  (let [new_items_chan (->> (to-chan coll)
;                            (async/map< filter_out_existing)
;                            (async/remove< nil?))]
;    (go-loop []
;             (when-let [new_item (<! new_items_chan)]
;               (debug "Publishing: " new_item)
;               (go
;                 (>! callback_chan new_item))
;               (go
;                 (wcar*
;                   (car/sadd coll_name new_item)))
;               (recur)))
;    ))





