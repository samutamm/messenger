(ns messenger.handler
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [compojure.core                   :refer [GET defroutes]]
            [compojure.handler                :as handler]
            [ring.util.response               :as resp]
            [ring.middleware.json             :as rj]
            [compojure.route                  :as route]))


(def conn (nr/connect "http://localhost:7474/db/data/" ))

(defn hello [] "Hello World")


(defroutes app-routes
  (GET "/" [] (resp/response (hello)))
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (-> app-routes
      (rj/wrap-json-response)))
