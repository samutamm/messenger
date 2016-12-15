(ns messenger.models.neo4j
  (:require [clojurewerkz.neocons.rest        :as nr]))

(def conn (nr/connect (or (System/getenv "NEO4J_PATH") "http://localhost:7474/db/data/")))
