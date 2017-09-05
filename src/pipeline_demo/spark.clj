(ns pipeline-demo.spark
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [network host-dir container-dir]
  "Starts Spark container, and run viyadb-spark job in it"
  (-> (tc/new-container "p7hb/docker-spark:latest")
      (tc/with-network network)
      (tc/with-filesystem-bind host-dir container-dir)
      (tc/with-command
        "sh" "-c"
        (str
          "wget -c https://github.com/viyadb/viyadb-spark/releases/download/v0.0.1/viyadb-spark_2.11-0.0.1-uberjar.jar && "
          "spark-submit --executor-memory 2G"
          " --conf spark.sql.shuffle.partitions=1"
          " --class com.github.viyadb.spark.streaming.Job viyadb-spark_2.11-0.0.1-uberjar.jar"
          " --table pipeline-demo"
          " --consul-host consul"))
      (tc/start)))
