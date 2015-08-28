(ns search-portal.helpers.view
    (:require [clojure.string :as s]
              [selmer.parser :as selmer]
              [selmer.filters :as filters]
              [selmer.parser :as parser]
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
    [args]
    (let [[text route & options] args]
        (format "<a href='%s'>%s</a>"
                (if (nil? options)
                    (route/url-for route)
                    (route/url-for route (read-string options)))
                text)))

(defn unescape [string]
    (s/replace
        string #"\\x(..)"
        (fn [m] (str (char (Integer/parseInt (second m) 16))))))

(def routes (atom nil))

(defn enhance-templates!
    "Add to template Context and Filters"
    [route-table]

    ;; Reset Routes
    (reset! routes route-table)

    ;; Add Filters
    (filters/add-filter! :embiginate clojure.string/upper-case)
    (filters/add-filter! :empty empty?)

    ;; Add Tags
    (parser/add-tag! :link-to
                     (fn [args context-map]
                         (let [url-for (route/url-for-routes @routes)
                               [rte dsp & opt] args
                               route-name (-> rte unescape read-string keyword)
                               display (-> dsp unescape read-string)
                               options (if (nil? opt) nil (-> opt unescape read-string))]
                             (format "<a href='%s'>%s</a>"
                                     (if (nil? options)
                                         (url-for route-name)
                                         (url-for route-name options))
                                     display))))
)

