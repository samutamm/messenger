(ns messenger.models.neo4j
  (:require [clojurewerkz.neocons.rest        :as nr]))

(def conn (nr/connect "http://localhost:7474/db/data/" ))
