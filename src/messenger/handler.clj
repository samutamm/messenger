(ns messenger.handler
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [compojure.core                   :refer [GET POST defroutes]]
            [compojure.handler                :as handler]
            [ring.util.response               :as resp]
            [ring.middleware.json             :as rj]
            [compojure.route                  :as route]
            [messenger.models.channels        :as channels]))

(defroutes app-routes
  (GET "/channels" [organization] (resp/response (channels/get-channels organization)))
  (POST "/channels/new" {body :body} (resp/response (channels/create-new-channel body)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn allow-cross-origin
  "middleware function to allow cross origin"
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
       (assoc-in [:headers "Access-Control-Allow-Origin"]  "*")
       (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,PUT,POST,DELETE,OPTIONS")
       (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control")))))

(def app
  (-> app-routes
      (handler/site)
      (allow-cross-origin)
      (rj/wrap-json-response)
      (rj/wrap-json-body)))
