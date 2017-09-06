(ns pipeline-demo.kafka
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.net :as net]
            [pipeline-demo.testcontainers :as tc]
            [clj-kafka.new.producer :as kp]))

(def ^:private host-ip (first (net/local-ips)))

(defn start-broker [network]
  (-> (tc/new-fixed-port-container "spotify/kafka:latest")
      (tc/with-network network "kafka")
      (tc/with-exposed-port 9092 9092)
      (tc/with-exposed-port 2181 2181)
      (tc/with-env {"ADVERTISED_HOST" host-ip})
      (tc/start))
  ; TODO: what waiting strategy to apply here?
  (Thread/sleep 10000))

(defn start-producer [channel]
  (future
    (with-open [p (kp/producer {"bootstrap.servers" (str host-ip ":9092")}
                               (kp/byte-array-serializer) (kp/byte-array-serializer))]
      (try 
        (while true
          (when-let [msg (async/<!! channel)]
            @(kp/send p (kp/record "events" (.getBytes msg)))))
        (catch Throwable e
          (log/error e))))))

