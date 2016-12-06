(defproject messenger "0.1.0-SNAPSHOT"
  :description "For slak messaging"
  :min-lein-version "2.0.0"
  :uberjar-name "messenger-standalone.jar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.immutant/web "2.0.0-beta2"]
                 [compojure "1.5.1"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [clojurewerkz/neocons "3.0.0"]
                 [ring/ring-json "0.3.1"]
                 [ring-cors "0.1.6"]
                 [environ "1.0.0"]]
  :plugins [[lein-ring "0.8.11"]
            [lein-immutant "2.0.0-alpha2"]]
  :main ^:skip-aot messenger.web
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]}
             :uberjar {:aot :all}})
