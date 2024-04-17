(ns klang.guestbook.web.controllers.health
  (:require
   [clojure.tools.logging :as log]
   [klang.guestbook.web.routes.utils :as utils]
   [ring.util.http-response :as http-response])
  (:import
    [java.util Date]))

(defn healthcheck!
  [req]
  ;; there are now two ways to get hold of the query-fn
  ;; deeply burried in the request
  (log/debug
   "query-fn"
   (let [query-fn (->> (tree-seq coll? seq (utils/route-data req))
                       (filter #(:query-fn %))
                       first
                       :query-fn)]
     (count (query-fn :get-messages {}))))
  ;; or lifted up to the surface via the reworked api.route-data and api.ig/init-key functions
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

(defn get-messages
  [req]
  (http-response/ok
    (let [{:keys [query-fn]} (utils/route-data req)]
                 (query-fn :get-messages {}))))
