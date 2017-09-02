(ns pipeline-demo.consul
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [network]
  "Starts Consul container, and returns mapped port number"
  (let [c (tc/new-container "consul:latest")]
    (tc/with-network c network "consul")
    (tc/with-exposed-ports c 8500)
    (tc/waiting-for-http c "/v1/health/service/consul")
    (tc/start c)
    (tc/mapped-port c 8500)))
