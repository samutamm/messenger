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

(def create-first-message-query
  "MATCH (org:Organization)-[:HAS_CHANNEL]->(channel)
    WHERE org.name = {organization} AND channel.name = {channel}
    CREATE (newMessage:Message {text: {message}, sender: {sender}}),
    (channel)-[:NEXT_MESSAGE]->(newMessage);")

(defn ten-latest-messages
  [org channel]
  (map (fn[x](get-in x ["messages" :data])) (cy/tquery neo4j/conn ten-latest-message-query
    {:organization (str org) :channel (str channel)})))

(defn create-new-message
  "There is three db-queries for each call of this function. Should be
  refactored to one query"
  [org channel message sender]
  (let [params {:organization (str org) :channel (str channel) :message (str message) :sender (str sender)}]
    do
    (if (= (count (ten-latest-messages org channel)) 0)
      (cy/tquery neo4j/conn create-first-message-query params)
      (cy/tquery neo4j/conn create-new-message-query params))
    (ten-latest-messages org channel)))
