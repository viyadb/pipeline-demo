(ns pipeline-demo.loader
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [me.raynes.fs :as fs]
            [pipeline-demo.viyadb :as viyadb])
  (:import [java.util.zip GZIPInputStream]))

(defn- is-new-file [last-file file]
  (and
    (re-matches #".*\.gz" (.getName file))
    (fs/exists? (fs/file (.getParentFile file) (str "." (.getName file) ".crc")))
    (or (nil? last-file) (> (.compareTo (.getPath file) last-file) 0))))

(defn- find-new-files [path last-file]
  (map #(.getPath %)
       (fs/find-files* path (partial is-new-file last-file))))

(defn- read-gzip-file [file]
  (with-open [in (GZIPInputStream. (io/input-stream file))]
    (slurp in)))

(defn start [input-dir]
  (log/info "Starting data loader")
  (future
    (try
      (loop [last-file nil]
        (Thread/sleep 10000)
        (let [new-files (find-new-files input-dir last-file)]
          (when (seq new-files)
            (log/debug "Found new data files:" new-files)
            (viyadb/load-data (map read-gzip-file new-files)))
          (recur (if (seq new-files) (last (sort new-files)) last-file))))
      (catch Throwable e
        (log/error e "Error loading files")))))
