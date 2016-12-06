(ns messenger.core
  (:require [messenger.handler     :as handler])
  (:gen-class))


(defn -main [& args]
  (handler/app args))
