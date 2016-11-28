(ns messenger.handler
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [compojure.core                   :refer [GET POST defroutes]]
            [compojure.handler                :as handler]
            [ring.util.response               :as resp]
            [ring.middleware.json             :as rj]
            [compojure.route                  :as route]))


(def conn (nr/connect "http://localhost:7474/db/data/" ))

(defn hello [] "Hello World")

(def channel-query "MATCH (org:Organization)-[:HAS_CHANNEL]-(channels)
                    WHERE org.name = {organization} return channels;")

(def create-channel "MATCH (org:Organization)
                    WHERE org.name = {organization}
                    CREATE (c:Channel {name: {newName}}),
                    (org)-[:HAS_CHANNEL]->(c);")

(defn get-channels
  [org]
  (let [result (cy/tquery conn channel-query {:organization (str org)})]
    result))

(defn create-new-channel
  [body]
  (let [organization (get body "organization")
        name (get body "channel")]
    (cy/tquery conn create-channel
      {:organization (str organization) :newName (str name)})))

(defroutes app-routes
  (GET "/" [] (resp/response (hello)))
  (GET "/channels" [organization] (resp/response (get-channels organization)))
  (POST "/channels/new" {body :body} (resp/response (create-new-channel body)))
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
