(ns pipeline-demo.viyadb
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [network]
  "Starts ViyaDB instance"
  (-> (tc/new-container "viyadb/viyadb:latest")
      (tc/with-exposed-ports 5000)
      (tc/with-network network "viyadb")
      (tc/waiting-for-http "/database/meta")
      (tc/start)))
