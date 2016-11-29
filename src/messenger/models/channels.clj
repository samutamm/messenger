(ns messenger.models.channels
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(def conn (nr/connect "http://localhost:7474/db/data/" ))

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
