(ns pipeline-demo.loader
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [me.raynes.fs :as fs])
  (:import [java.util.zip GZIPInputStream]))

(defn find-new-files [path last-file]
  ; XXX - return only finished files, which is called .part-00000.gz.crc
  (filter #(or (nil? last-file) (> (.compareTo % last-file) 0))
    (map #(.getPath %) (fs/find-files path #".*\.gz"))))

(defn start [source-dir target-dir]
  (let [tmp-file (str target-dir "/data.tsv")]
    (future
      (try
        (loop [last-file nil]
          (Thread/sleep 10000)
          (let [new-files (find-new-files source-dir last-file)]
            (when (seq new-files)
              (with-open [out (io/writer tmp-file)]
                (doseq [f new-files]
                  (with-open [in (GZIPInputStream. (io/input-stream f))]
                    (.write out (slurp in)))))
              ; XXX - load to ViyaDB
              )
            (recur (if (seq new-files) (last (sort new-files)) last-file))))
        (catch Throwable e
          (log/error e "Error loading files"))))))
