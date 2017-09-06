(ns pipeline-demo.spark
  (:require [clojure.tools.logging :as log]
            [pipeline-demo.config :as config]
            [pipeline-demo.testcontainers :as tc]))

(defn start [network]
  "Starts Spark container, and run viyadb-spark job in it. Returns results directory path."
  (let [host-dir (str config/tmp-dir "/spark")]
    (-> (tc/new-container "p7hb/docker-spark:latest")
        (tc/with-network network)
        (tc/with-filesystem-bind host-dir (:deepStorePath config/table-conf))
        (tc/with-command
          "sh" "-c"
          (str
            "wget -c https://github.com/viyadb/viyadb-spark/releases/download/v0.0.1/viyadb-spark_2.11-0.0.1-uberjar.jar && "
            "spark-submit --executor-memory 2G"
            " --conf spark.sql.shuffle.partitions=1"
            " --class com.github.viyadb.spark.streaming.Job viyadb-spark_2.11-0.0.1-uberjar.jar"
            " --table pipeline-demo"
            " --consul-host consul"))
        (tc/start))
    (str host-dir "/realtime")))
