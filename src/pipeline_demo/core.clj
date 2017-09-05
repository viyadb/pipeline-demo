(ns pipeline-demo.core
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [pipeline-demo.testcontainers :as tc]
            [pipeline-demo.consul :as consul]
            [pipeline-demo.config :as config]
            [pipeline-demo.kafka :as kafka]
            [pipeline-demo.spark :as spark]
            [pipeline-demo.viyadb :as viyadb]
            [pipeline-demo.events :as events]
            [pipeline-demo.loader :as loader]
            [me.raynes.fs :as fs])
  (:gen-class))

(defn make-temp-dir []
  (let [dir (.getPath (fs/temp-dir "pipeline-demo-"))]
    (log/info (str "Created temporary directory:" dir))
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(fs/delete-dir dir)))
    dir))

(defn write-table-conf [consul-port]
  (http/put
    (str "http://localhost:" consul-port "/v1/kv/viyadb-cluster/pipeline-demo/table")
    {:body (json/generate-string config/table-conf)}))

(defn create-table [viyadb-port]
  (log/info "Creating table in ViyaDB")
  (http/post
    (str "http://localhost:" viyadb-port "/tables")
    {:body (json/generate-string config/table-conf)}))

(defn -main []
  (let [channel (async/chan)
        tmp-dir (make-temp-dir)
        spark-output-dir (str tmp-dir "/spark")
        viyadb-input-dir (str tmp-dir "/viyadb")
        network (tc/new-network)
        consul-port (consul/start-docker network)]
    (write-table-conf consul-port)
    (kafka/start-docker network)
    (kafka/start-producer channel)
    (events/start-docker channel)
    (-> (viyadb/start-docker
          network viyadb-input-dir "/tmp/pipeline-demo")
        (create-table))
    (loader/start
      (str spark-output-dir "/realtime") viyadb-input-dir)
    (spark/start-docker
      network spark-output-dir (:deepStorePath config/table-conf))))
