(ns search-portal.helpers.view
    (:require [selmer.parser :as selmer]
              [selmer.filters :as filters]
              [ring.util.response :as ring-resp]
              [io.pedestal.http.route :as route]))

(comment
    ;; Control Template Caching
    (selmer/cache-on!)
    (selmer/cache-off!)
    )

(defn render-only
    "Only renders a string template and sends the response"
    [tmpl-string data]
    (selmer/render tmpl-string data))

(defn render
    "Renders a string template and sends the response"
    [tmpl-string data]
    (ring-resp/response
        (selmer/render tmpl-string data)))

(defn render-file-only
    "Only renders a file template"
    [filename context-map]
    (selmer/render filename context-map))

(defn render-file
    "Renders a file template and sends the response"
    [filename context-map]
    (ring-resp/response
        (selmer/render-file filename context-map)))

(defn link-to
    "Uses io.pedestal.http.route/url-for fn to generate a link to a named route"
    [text route & options]
    (format "<a href='%s'>%s</a>"
            (route/url-for route options)
            text))

(defn enhance-templates!
    "Add to template Context and Filters"
    []
    ;; Add Filters
    (filters/add-filter! :embiginate clojure.string/upper-case)
    (filters/add-filter! :empty empty?))

