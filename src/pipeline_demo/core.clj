(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]
            [pipeline-demo.consul :as consul]
            [pipeline-demo.config :as config]
            [pipeline-demo.kafka :as kafka]
            [pipeline-demo.spark :as spark]
            [pipeline-demo.viyadb :as viyadb]
            [pipeline-demo.events :as events])
  (:gen-class))

(defn -main []
  (let [channel (async/chan)
        network (tc/new-network)
        consul-port (consul/start-docker network)]
    (config/init consul-port)
    (kafka/start-docker network)
    (kafka/start-producer channel)
    (spark/start-docker network consul-port)
    (viyadb/start-docker network)
    (events/start-docker channel)))
