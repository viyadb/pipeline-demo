(ns pipeline-demo.spark
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.config :as config]
            [pipeline-demo.testcontainers :as tc]))

(defn start [network]
  "Starts Spark container, and run viyadb-spark job in it. Returns results directory path."
  (-> (tc/new-container "p7hb/docker-spark:latest")
      (tc/with-network network)
      (tc/with-filesystem-bind config/tmp-dir (:deepStorePath config/indexer-conf))
      (tc/with-filesystem-bind (.getCanonicalPath (clojure.java.io/file ".")) "/resources")
      (tc/with-command
        "sh" "-c"
        (str
          "spark-submit --executor-memory 2G"
          " --conf spark.sql.shuffle.partitions=1"
          " --class com.github.viyadb.spark.streaming.Job /resources/viyadb-spark*.jar"
          " --indexer-id pipeline-demo"
          " --consul-host consul"))
      (tc/start)))
