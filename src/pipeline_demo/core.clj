(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]
            [pipeline-demo.consul :as consul]
            [pipeline-demo.kafka :as kafka]
            [pipeline-demo.spark :as spark]
            [pipeline-demo.viyadb :as viyadb]
            [pipeline-demo.events :as events]
            [pipeline-demo.loader :as loader])
  (:gen-class))

(defn -main []
  (let [channel (async/chan)
        network (tc/new-network)]
    (consul/start network)
    (kafka/start-broker network)
    (kafka/start-producer channel)
    (events/start channel)
    (viyadb/start network)
    (-> (spark/start network)
        (loader/start))))
