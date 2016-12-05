(ns messenger.handler
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [compojure.core                   :refer [GET POST defroutes]]
            [compojure.handler                :as handler]
            [ring.util.response               :as resp]
            [ring.middleware.json             :as rj]
            [ring.middleware.cors             :refer [wrap-cors]]
            [compojure.route                  :as route]
            [messenger.models.channels        :as channels]))

(defroutes app-routes
  (GET "/channels" [organization] (resp/response (channels/get-channels organization)))
  (POST "/channels/new" {body :body} (let [org (get body "organization") name (get body "channel")]
                                        (resp/response (channels/create-new-channel org name))))
  (POST "/channels/join" [organization channel username] (resp/response (channels/join-channel
                                        organization channel username)))
  (POST "/channels/quit" [organization channel username] (resp/response (channels/quit-channel
                                        organization channel username)))
  (GET "/channels/:username" [organization username] (resp/response
                                                      (channels/get-users-channels organization username)))
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
      (rj/wrap-json-response)
      (rj/wrap-json-body)
      (wrap-cors :access-control-allow-origin #"http://localhost:3000"
                 :access-control-allow-methods [:get :put :post :options]
                 :access-control-allow-headers ["Origin" "X-Requested-With" "Content-Type" "X-Auth-Token" "Accept"])))
