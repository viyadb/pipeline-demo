(ns pipeline-demo.testcontainers
  (:import [org.testcontainers.containers Network Container GenericContainer FixedHostPortGenericContainer]
           [org.testcontainers.images.builder ImageFromDockerfile]
           [com.github.dockerjava.api.model LogConfig]
           [java.util.function Consumer]))

(defn new-network []
  (Network/newNetwork))

(defn new-container [image]
  (GenericContainer. image))

(defn new-fixed-port-container [image]
  (FixedHostPortGenericContainer. image))

(defn new-image [image-name instructions]
  (-> (ImageFromDockerfile. image-name)
      (.withFileFromString "Dockerfile" (clojure.string/join "\n" instructions))))

(defn with-command [^Container container command & args]
  (if (seq args)
    (.withCommand container (into-array String (map str (cons command args))))
    (.withCommand container ^String command)))

(defn with-exposed-ports [^Container container & ports]
  (.withExposedPorts container (into-array Integer (map int ports))))

(defn with-exposed-port [^FixedHostPortGenericContainer container host-port container-port]
  (.withFixedExposedPort container host-port container-port))

(defn with-env [^Container container m]
  (.withEnv container (java.util.HashMap. m)))

(defn with-network [^Container container ^Network network & aliases]
  (-> container
      (.withNetwork network)
      (.withNetworkAliases (into-array String aliases))))

(defn with-logs-disabled [^Container container]
  (.withCreateContainerCmdModifier container
                                   (reify Consumer
                                     (accept [this cmd]
                                       (.withLogConfig cmd (LogConfig. com.github.dockerjava.api.model.LogConfig$LoggingType/NONE))))))

(defn start [^Container container]
  (.start container))

(defn follow-output [^Container container callback]
  (.followOutput container
                 (reify Consumer
                   (accept [this output-frame]
                     (when-let [b (.getBytes output-frame)]
                       (callback (clojure.string/trim-newline (apply str (map char b)))))))))

(defn mapped-port [^Container container port]
  (.getMappedPort container port))

