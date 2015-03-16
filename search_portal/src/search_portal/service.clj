(ns search-portal.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.interceptor :refer [definterceptor]]
            [search-portal.helpers.view :as view]
            [clj-http.client :as client]))

;(defn about-page
;  [request]
;  (view/render (format "Clojure %s - served from %s"
;                              (clojure-version)
;                              (route/url-for ::about-page))))

;(defn home-page
;  [request]
;  (ring-resp/response "Hello World!"))

(view/enhance-templates!)

;; Check this out: https://www.google.com/search?client=opera&q=clojure+pedestal+interceptors+example&sourceid=opera&ie=UTF-8&oe=UTF-8
;; Check this out: http://stackoverflow.com/questions/17797647/how-to-write-a-simple-error-interceptor
;; Check this out: http://frankiesardo.github.io/blog/2014/12/15/give-pedestal-another-chance/

(defn home-page
  [request]
  (view/render-file "templates/home.jinja2"
                    {:page "Home"
                     :summary "Hello World!!!"}))

(defn about-page
    [request]
    (view/render-file "templates/home.jinja2"
                      {:page "About"
                       :summary (view/render-only "{{ shout|embiginate }}"
                                                   {:shout (format "Clojure %s - served from %s"
                                                                   (clojure-version)
                                                                   (route/url-for ::about-page))})}))

(defn proxied-page
    [request]
    (let [gpage (client/get "http://google.com")]
        gpage))

(defn gae-start
    [request]
    (view/render-file"templates/home.jinja2"
                     {:page "Start"
                      :summary "Application Started"}))

(defn gae-health
    [request]
    (view/render-file "templates/home.jinja2"
                      {:page "Health"
                       :summary "Healthy Application"}))

(definterceptor not-found []
                (interceptor
                    :error (fn [context error]
                               (assoc context :response
                                              {:status 404
                                               :body (view/render-file-only "templates/error/404.jinja2"
                                                                            {:page "404 Not Found"
                                                                             :message (str error)})
                                               :headers {"Content-Type" "text/html"}}))
                    ))

(defroutes routes
  [[["/" {:get home-page}
     ;; Set default interceptors for /about and any other paths under /
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/about" {:get about-page}]
     ["/goog" {:get proxied-page}]
     ;; GAE Application Lifecycle Handlers
     ["/_ah/start" {:get gae-start}]
     ["/_ah/health" {:get gae-health}]]]])

;; Consumed by search-portal.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ;::bootstrap/resource-path "/templates"
              ::bootstrap/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;::bootstrap/type :immutant
              ::bootstrap/type :jetty
              ;;::bootstrap/host "localhost"
              ::bootstrap/port 8080
              })

