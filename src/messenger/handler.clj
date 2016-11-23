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

(def channel-query "MATCH (org:Organization)-[:HAS_CHANNEL]-(channels)
                    WHERE org.name = {organization} return channels;")

(def create-channel "MATCH (org:Organization)
                    WHERE org.name = {organization}
                    CREATE (c:Channel {name: {newName}}),
                    (org)-[:HAS_CHANNEL]->(c);")

(defn get-channels
  [org]
  (let [[result] (cy/tquery conn channel-query {:organization (str org)})]
    result))


(defroutes app-routes
  (GET "/" [] (resp/response (hello)))
  (GET "/channels" [organization] (resp/response (get-channels organization)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (handler/site)
      (rj/wrap-json-response)))
