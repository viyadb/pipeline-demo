(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.consul :as consul]
            [pipeline-demo.kafka :as kafka]
            [pipeline-demo.events :as events])
  (:gen-class))

(defn -main []
  (let [channel (async/chan)
        consul-port (consul/start-docker)]
    (kafka/start-docker)
    (kafka/start-producer channel)
    (events/start-docker channel)))
