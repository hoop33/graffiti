(ns graffiti.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [graffiti.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity posts)

(defn create-post [text]
  (insert posts
          (values {:text text
                   :created_at (new java.util.Date)})))

(defn get-posts []
  (select posts))

