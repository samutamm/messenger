(ns messenger.web
  (:require [clojurewerkz.neocons.rest        :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]
            [compojure.core                   :refer [GET POST defroutes]]
            [compojure.route                  :as route]
            [compojure.handler                :as handler]
            [ring.adapter.jetty               :as ring]
            [ring.util.response               :as resp]
            [ring.middleware.json             :as rj]
            [ring.middleware.cors             :refer [wrap-cors]]
            [compojure.route                  :as route]
            [messenger.models.channels        :as channels]
            [messenger.models.messages        :as messages])
            (:gen-class))

(defroutes routes
  (GET "/channels" [organization] (resp/response (channels/get-channels organization)))
  (POST "/channels/new" {body :body} (let [org (get body "organization") name (get body "channel")]
                                        (resp/response (channels/create-new-channel org name))))
  (POST "/channels/join" [organization channel username] (resp/response (channels/join-channel
                                        organization channel username)))
  (POST "/channels/quit" [organization channel username] (resp/response (channels/quit-channel
                                        organization channel username)))
  (GET "/channels/:username" [organization username] (resp/response
                                                      (channels/get-users-channels organization username)))
  (POST "/messages/new" [organization channel message sender] (resp/response (messages/create-new-message
                                        organization channel message sender)))
  (GET "/messages" [organization channel] (resp/response (messages/ten-latest-messages
                                        organization channel)))
  (route/resources "/")
  (route/not-found "Not Found"))


(defn cors-ring-handler
  "Wraps the ring handler to support CORS"
  [handler]
  (fn [request]
    (let [resp (handler request)]
      ; If the response is nil, don't pass through ring-cors
      (if ((complement nil?) resp)
        (let [cors-wrapped-handler
              (wrap-cors :access-control-allow-origin [#"https://samu-slak.herokuapp.com"]
                         :access-control-allow-methods [:get :put :post :options]
                         :access-control-allow-headers ["Origin" "X-Requested-With" "Content-Type" "X-Auth-Token" "Accept"])]
          (cors-wrapped-handler request))))))

(def application
  (-> routes
    (handler/site)
    (rj/wrap-json-response)
    (rj/wrap-json-body)
    (cors-ring-handler)

(defn start [port]
 (ring/run-jetty application {:port port
                              :join? false}))
(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
