(ns graffiti.handler
  (:require [compojure.core :refer [defroutes]]
            [graffiti.routes.home :refer [home-routes]]
            [graffiti.middleware :as middleware]
            [noir.util.middleware :refer [app-handler]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [graffiti.models.schema :as schema]
            [environ.core :refer [env]]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "graffiti.log" :max-size (* 512 1024) :backlog 10})

  ;;intialize the database
  (if-not (schema/initialized?) (schema/create-tables))

  (if (env :dev) (parser/cache-off!))
  (timbre/info "graffiti started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "graffiti is shutting down..."))



(def app (app-handler
           ;; add your application routes here
           [home-routes app-routes]
           ;; add custom middleware here
           :middleware [middleware/template-error-page
                        middleware/log-request]
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn]))
