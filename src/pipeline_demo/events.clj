(ns pipeline-demo.events
  (:require [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [pipeline-demo.testcontainers :as tc]))

(defn start-docker [channel]
  "Starts events generator, and redirect its output to the channel"
  (let [c (tc/new-container "viyadb/events-generator:latest")]
    (tc/start c)
    (tc/follow-output c (fn [s] (async/>!! channel s)))))

