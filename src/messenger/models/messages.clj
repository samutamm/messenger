(ns messenger.models.messages
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [messenger.models.neo4j           :as neo4j]))

(def ten-latest-message-query
  "MATCH (org:Organization)-[:HAS_CHANNEL]-(channel)-[:NEXT_MESSAGE*1..10]-(messages)
    WHERE org.name = {organization} AND channel.name = {channel} return messages;")

(def create-new-message-query
  "MATCH (org:Organization)-[:HAS_CHANNEL]-(channel)-[old_rel:NEXT_MESSAGE]->(oldNewMessage)
    WHERE org.name = {organization} AND channel.name = {channel}
    DELETE old_rel
    CREATE (newMessage:Message {text: {message}, sender: {sender}}),
    (channel)-[:NEXT_MESSAGE]->(newMessage),
    (newMessage)-[:NEXT_MESSAGE]->(oldNewMessage);")

(defn create-new-message
  [org channel message sender]
  (cy/tquery neo4j/conn create-new-message-query
    {:organization (str org) :channel (str channel) :message (str message) :sender (str sender)}))

(defn ten-latest-messages
  [org channel]
  (map (fn[x](get-in x ["messages" :data])) (cy/tquery neo4j/conn ten-latest-message-query
    {:organization (str org) :channel (str channel)})))
