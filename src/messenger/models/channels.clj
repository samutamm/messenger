(ns messenger.models.channels
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [messenger.models.neo4j           :as neo4j]))

(def channel-query "MATCH (org:Organization)-[:HAS_CHANNEL]-(channels)
                    WHERE org.name = {organization} return channels;")

(def create-channel-query "MATCH (org:Organization)
                          WHERE org.name = {organization}
                          CREATE (c:Channel {name: {newName}}),
                          (org)-[:HAS_CHANNEL]->(c);")

(def join-channel-query "MATCH (org:Organization)-[:HAS_CHANNEL]-(channel)
                        WHERE org.name = {organization} AND channel.name = {channelName}
                        MATCH (org)-[:HAS_USER]-(user) WHERE user.name = {username}
                        CREATE (channel)-[:HAS_MEMBER]->(user), (user)-[:IS_MEMBER]->(channel);")

(def get-users-channels "MATCH (org:Organization)-[:HAS_USER]-(user)
                        WHERE user.name = {username} AND org.name = {organization}
                        MATCH (user)-[:IS_MEMBER]-(channels) return channels;")

(def get-channels-members "MATCH (org:Organization)-[:HAS_CHANNEL]-(channel)
                          WHERE org.name = {organization} AND channel.name = {channelName}
                          MATCH (channel)-[:HAS_MEMBER]-(users) return users")

(defn get-channels
  [org]
  (let [result (cy/tquery neo4j/conn channel-query {:organization (str org)})]
    result))

(defn create-new-channel
  [body]
  (let [organization (get body "organization")
        name (get body "channel")]
    (cy/tquery neo4j/conn create-channel-query
      {:organization (str organization) :newName (str name)})))

(defn join-channel
  [org channel username]
  nil)
