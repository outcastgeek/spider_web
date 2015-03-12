(ns search-portal.helpers.view
    (:require [selmer.parser :as selmer]
              [selmer.filters :as filters]
              [ring.util.response :as ring-resp]))

(defn enhance-templates!
    []
    ;; Add Filters
    (filters/add-filter! :embiginate clojure.string/upper-case)
    (filters/add-filter! :empty empty?))

(comment
    ;; Control Template Caching
    (selmer/cache-on!)
    (selmer/cache-off!)
    )

(defn render-only
    [tmpl-string data]
    (selmer/render tmpl-string data))

(defn render
    [tmpl-string data]
    (ring-resp/response
        (selmer/render tmpl-string data)))

(defn render-file-only
    [filename context-map]
    (selmer/render filename context-map))

(defn render-file
    [filename context-map]
    (ring-resp/response
        (selmer/render-file filename context-map)))
