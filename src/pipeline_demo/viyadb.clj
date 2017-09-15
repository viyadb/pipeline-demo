(ns pipeline-demo.viyadb
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [me.raynes.fs :as fs]
            [pipeline-demo.config :as config]
            [pipeline-demo.testcontainers :as tc]))

(def ^:private local-conf (atom {:container-dir "/tmp/pipeline-demo"}))

(defn- count->long_sum [metric]
  "Replace count metric with long_sum as counting happens on Spark side"
  (if (= (:type metric) "count")
    (assoc metric :type "long_sum")
    metric))

(defn- adapt-config [table-conf]
  "Adapt table configuration before passing it to ViaDB"
  (update-in table-conf [:metrics] (partial map count->long_sum)))

(defn- create-table []
  (log/info "Creating table in ViyaDB")
  (http/post
    (str "http://localhost:" (:mapped-port @local-conf) "/tables")
    {:body (json/generate-string (adapt-config config/table-conf))}))

(defn load-data [chunks]
  (log/info "Loading data into ViyaDB instance")
  (with-open [out (io/writer (str (:host-dir @local-conf) "/data.tsv"))]
    (doseq [c chunks] (.write out c)))
  (http/post
    (str "http://localhost:" (:mapped-port @local-conf) "/load")
    {:body (json/generate-string
             {:table (:name config/table-conf)
              :format "tsv"
              :type "file"
              :file (str (:container-dir @local-conf) "/data.tsv")})}))

(defn start [network]
  "Starts ViyaDB instance, and creates a table in it"
  (let [host-dir (str config/tmp-dir "/viyadb")
        c (tc/new-container "viyadb/viyadb:latest")]
    (swap! local-conf assoc :host-dir host-dir)
    (fs/mkdir (fs/file host-dir))
    (tc/with-exposed-ports c 5000)
    (tc/with-network c network "viyadb")
    (tc/with-filesystem-bind c host-dir (:container-dir @local-conf))
    (tc/waiting-for-http c "/database/meta")
    (tc/start c)
    (swap! local-conf assoc :mapped-port (tc/mapped-port c 5000))
    (create-table)))

