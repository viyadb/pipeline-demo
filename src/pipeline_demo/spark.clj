(ns pipeline-demo.spark
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [network consul-port]
  "Starts Spark container, and run viyadb-spark job in it"
  (-> (tc/new-container "p7hb/docker-spark:latest")
      (tc/with-network network)
      (tc/with-command
        "sh" "-c"
        (str
          "wget -c https://github.com/viyadb/viyadb-spark/releases/download/v0.0.1/viyadb-spark_2.11-0.0.1-uberjar.jar && "
          "spark-submit --executor-memory 2G"
          " --class com.github.viyadb.spark.streaming.Job viyadb-spark_2.11-0.0.1-uberjar.jar"
          " --table pipeline-demo"
          " --consul-host consul"))
      (tc/start)))
