(ns klang.guestbook.web.controllers.health
  (:require
   [clojure.tools.logging :as log]
   [klang.guestbook.web.routes.utils :as utils]
   [ring.util.http-response :as http-response])
  (:import
    [java.util Date]))

(defn healthcheck!
  [req]
  (log/debug
   "query-fn"
   (let [query-fn (->> (tree-seq coll? seq (utils/route-data req))
                       (filter #(:query-fn %))
                       first
                       :query-fn)]
     (count (query-fn :get-messages {}))))

  (log/debug "query-fn"
             (let [{:keys [query-fn]} (utils/route-data req)]
               (count (query-fn :get-messages {}))))

  (http-response/ok
    {:time     (str (Date. (System/currentTimeMillis)))
     :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
     :app      {:status  "up"
                :message ""}
     :db     (let [{:keys [query-fn]} (utils/route-data req)
                   items (count (query-fn :get-messages {}))]
                 {:status "up"
                  :message (str "count: " items)})}))
