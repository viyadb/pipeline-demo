(ns pipeline-demo.viyadb
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [network host-dir container-dir]
  "Starts ViyaDB instance"
  (let [c (tc/new-container "viyadb/viyadb:latest")]
    (tc/with-exposed-ports c 5000)
    (tc/with-network c network "viyadb")
    (tc/with-filesystem-bind c host-dir container-dir)
    (tc/waiting-for-http c "/database/meta")
    (tc/start c)
    (tc/mapped-port c 5000)))
