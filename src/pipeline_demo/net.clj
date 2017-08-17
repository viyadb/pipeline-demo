(ns pipeline-demo.net
  (:import [java.net NetworkInterface Inet4Address]))

(defn- iface-filter [iface]
  (and (.isUp iface)
       (not (.isVirtual iface))
       (not (.isLoopback iface))))

(defn- address-filter [address]
  (instance? Inet4Address address))

(defn- address->ip [address]
  (.getHostAddress address))

(defn- ip-extract [iface]
  (->> (enumeration-seq (.getInetAddresses iface))
       (filter address-filter)
       (map address->ip)))

(defn local-ips []
  (->> (NetworkInterface/getNetworkInterfaces)
       (enumeration-seq)
       (filter iface-filter)
       (mapcat ip-extract)))
