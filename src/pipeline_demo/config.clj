(ns pipeline-demo.config
  (:require [clojure.tools.logging :as log]
            [me.raynes.fs :as fs]))

(def table-conf 
  {:name "pipeline-demo"
   :deepStorePath "/tmp/events_store"
   :realTime {:windowDuration "PT1M"
              :kafka {:topics ["events"]
                      :brokers ["kafka:9092"]}
              :parseSpec {:format "json"}}
   :batch {:partitioning {:column "app_id"
                          :hashColumn false
                          :numPartitions 3}}
   :dimensions [{:name "app_id"}
                {:name "event_time" :type "time" :granularity "day"}
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
             {:name "count" :type "count"}]
   :timeColumn {:name "event_time"}})


(def tmp-dir
  (let [d (.getPath (fs/temp-dir "pipeline-demo-"))]
    (log/info (str "Created temporary directory:" d))
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(fs/delete-dir d)))
    d))
