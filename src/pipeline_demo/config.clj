(ns pipeline-demo.config
  (:require [clojure.tools.logging :as log]
            [me.raynes.fs :as fs]))

(def table-conf 
  {:name "pipeline-demo"
   :dimensions [{:name "app_id"}
                {:name "event_time" :type "time" :format "millis" :granularity "day"}
                {:name "country"}
                {:name "city"}
                {:name "device_type"}
                {:name "device_vendor"}
                {:name "ad_network"}
                {:name "campaign"}
                {:name "site_id"}
                {:name "event_type"}
                {:name "event_name"}]
   :metrics [{:name "revenue" :type "double_sum"}
             {:name "count" :type "count"}]})

(def indexer-conf 
  {:tables ["pipeline-demo"]
   :deepStorePath "/tmp/pipeline-demo"
   :realTime {:windowDuration "PT30S"
              :kafkaSource {:topics ["events"]
                            :brokers ["kafka:9092"]}
              :parseSpec {:format "json"
                          :timeColumn {:name "event_time"}}
              :notifier {:type "kafka"
                         :channel "kafka:9092"
                         :queue "rt-notifications"}}
   :batch {:partitioning {:columns ["app_id"]
                          :partitions 2}
           :notifier {:type "kafka"
                      :channel "kafka:9092"
                      :queue "batch-notifications"}}})

(def cluster-conf 
  {:replication_factor 1
   :tables ["pipeline-demo"]
   :indexers ["pipeline-demo"]})

(def store-conf
  {:supervise true
   :workers 2
   :cluster_id "pipeline-demo"
   :consul_url "http://consul:8500"
   :state_dir "/tmp/viyadb"})

(def tmp-dir
  (let [d (.getPath (fs/temp-dir "pipeline-demo-"))]
    (log/info (str "Created temporary directory:" d))
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(fs/delete-dir d)))
    d))
