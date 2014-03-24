(ns graffiti.routes.home
  (:use compojure.core)
  (:require [graffiti.views.layout :as layout]
            [graffiti.models.db :as db]
            [graffiti.util :as util]))

(defn home-page [& [error]]
  (layout/render
    "home.html" 
    {:error error
     :posts (db/get-posts)}))

(defn create-post [text]
  (cond
    
    (empty? text)
    (home-page "You must specify text")
    
    :else
    (do
      (db/create-post text)
      (home-page))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" [text] (create-post text))
  (GET "/about" [] (about-page)))
