(defproject messenger "0.1.0-SNAPSHOT"
  :description "For slak messaging"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.5.1"]
                 [clojurewerkz/neocons "3.0.0"]
                 [ring/ring-json "0.3.1"]
                 [ring-cors "0.1.6"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler messenger.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}})
