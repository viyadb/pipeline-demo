(defproject pipeline-demo "0.1.0"
  :description "Real-time pipeline demo for ViyaDB"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.13"]
                 [org.slf4j/log4j-over-slf4j "1.7.13"]
                 [org.testcontainers/testcontainers "1.4.2"]
                 [clj-kafka "0.3.4"]]
  :main pipeline-demo.core
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all
                       :dependencies [[ch.qos.logback/logback-classic "1.2.3"]]}
             :dev {:dependencies [[org.slf4j/slf4j-simple "1.7.13"]]
                   :jvm-opts ["-Dorg.slf4j.simpleLogger.defaultLogLevel=INFO"]}})
