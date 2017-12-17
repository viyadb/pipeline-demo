(ns pipeline-demo.viyadb
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [pipeline-demo.config :as config]
            [pipeline-demo.testcontainers :as tc]))

(defn start [network]
  "Starts ViyaDB instance, and creates a table in it"
  (spit (str config/tmp-dir "/store.json") (json/generate-string config/store-conf))
  (-> (tc/new-container "viyadb/viyadb:latest")
      (tc/with-exposed-ports 5000)
      (tc/with-network network "viyadb")
      (tc/with-filesystem-bind config/tmp-dir (:deepStorePath config/indexer-conf))
      (tc/waiting-for-http "/database/meta")
      (tc/with-command "/opt/viyadb/bin/viyad" (str (:deepStorePath config/indexer-conf) "/store.json"))
      (tc/start)))
