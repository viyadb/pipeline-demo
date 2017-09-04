(ns pipeline-demo.config
  (:require [clojure.tools.logging :as log]))

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
                {:name "ad_network"}
                {:name "campaign"}
                {:name "site_id"}
                {:name "event_type"}
                {:name "event_name"}]
   :metrics [{:name "revenue" :type "double_sum"}
             {:name "count" :type "count"}]
   :timeColumn {:name "event_time"}})
