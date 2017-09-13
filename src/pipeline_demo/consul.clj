(ns pipeline-demo.consul
  (:require [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [pipeline-demo.config :as config]
            [pipeline-demo.testcontainers :as tc]))

(defn- write-table-conf [consul-port]
  (http/put
    (str "http://localhost:" consul-port "/v1/kv/viyadb-cluster/tables/pipeline-demo/config")
    {:body (json/generate-string config/table-conf)}))

(defn start [network]
  "Starts Consul container, and initialize it with configuration"
  (let [c (tc/new-container "consul:latest")]
    (tc/with-network c network "consul")
    (tc/with-exposed-ports c 8500)
    (tc/waiting-for-http c "/v1/health/service/consul")
    (tc/start c)
    (write-table-conf (tc/mapped-port c 8500))))
