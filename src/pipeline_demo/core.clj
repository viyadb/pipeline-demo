(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [pipeline-demo.testcontainers :as tc]
            [pipeline-demo.consul :as consul]
            [pipeline-demo.config :as config]
            [pipeline-demo.kafka :as kafka]
            [pipeline-demo.spark :as spark]
            [pipeline-demo.viyadb :as viyadb]
            [pipeline-demo.events :as events])
  (:gen-class))

(defn write-table-conf [consul-port]
  (http/put
    (str "http://localhost:" consul-port "/v1/kv/viyadb-cluster/pipeline-demo/table")
    {:body (json/generate-string config/table-conf)}))

(defn create-table [viyadb-port]
  (http/post
    (str "http://localhost:" viyadb-port "/tables")
    {:body (json/generate-string config/table-conf)}))

(defn -main []
  (let [channel (async/chan)
        tmp-dir "/tmp/pipeline-demo"
        network (tc/new-network)
        consul-port (consul/start-docker network)]
    (.mkdirs (io/file tmp-dir))
    (write-table-conf consul-port)
    (kafka/start-docker network)
    (kafka/start-producer channel)
    (events/start-docker channel)
    (-> (viyadb/start-docker network)
        (create-table))
    (spark/start-docker
      network tmp-dir (:deepStorePath config/table-conf))))
