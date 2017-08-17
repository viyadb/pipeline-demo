(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.net :as net]
            [pipeline-demo.testcontainers :as tc]
            [clj-kafka.new.producer :as kp])
  (:gen-class))

(defn start-kafka [host-ip]
  (-> (tc/new-fixed-port-container "spotify/kafka:latest")
      (tc/with-exposed-port 9092 9092)
      (tc/with-exposed-port 2181 2181)
      (tc/with-env {"ADVERTISED_HOST" host-ip})
      (tc/start))
  ; TODO: what waiting strategy to apply here?
  (Thread/sleep 10000))

(defn start-events-generator [channel]
  (let [c (tc/new-container "viyadb/events-generator:latest")]
    (tc/start c)
    (tc/follow-output c (fn [s] (async/>!! channel s)))))

(defn start-kafka-producer [channel host-ip]
  (future
    (with-open [p (kp/producer {"bootstrap.servers" (str host-ip ":9092")}
                               (kp/byte-array-serializer) (kp/byte-array-serializer))]
      (try 
        (while true
          (when-let [msg (async/<!! channel)]
            @(kp/send p (kp/record "test" (.getBytes msg)))))
        (catch Throwable e
          (log/error e))))))

(defn -main []
  (let [channel (async/chan)
        host-ip (first (net/local-ips))]
    (log/info "Host IP detected as:" host-ip)
    (start-kafka host-ip)
    (start-kafka-producer channel host-ip)
    (start-events-generator channel)
  ))
