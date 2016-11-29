(ns messenger.models.user
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [messenger.models.neo4j           :as neo4j]))

(def create-user-query "MATCH (org:Organization)
                        WHERE org.name = {organization}
                        CREATE (u:User {name: {username}}),
                        (org)-[:HAS_USER]->(u);")

(def user-query "MATCH (org:Organization)-[:HAS_USER]-(user)
                WHERE org.name = {organization} AND user.name = {username}
                return user;")

(defn user-exists
  [org username]
  (let [result (cy/tquery neo4j/conn user-query {:organization (str org) :username (str username)})]
    (= (count result) 1)))

(defn create-user-if-not-exist
  [org username]
  (if (not (user-exists org username))
    (cy/tquery neo4j/conn create-user-query {:organization (str org) :username (str username)})))
