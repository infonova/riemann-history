(ns riemann-history.core
  (:require [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [clj-time.coerce :as clt-c]
            [clj-time.format :as clt-f]
            [qbits.spandex :as es]
            [riemann.time :refer [every!]]))

(def history-data 
  (atom {:default {}}))

(defn get-history-data
  "Get history from `history-data` atom."
  [hk ek]
  (get-in @history-data [hk ek]))

(def key-formatter 
  (clt-f/formatter "e:H"))

(defn generate-key-from-epoch
  "Get history-data key from event epoch."
  [epoch]
  (keyword (clt-f/unparse key-formatter (clt-c/from-long (long (* 1000 epoch))))))

(defn transform
  "Transforms Elasticsearch buckets vector to map. E.g.

  `[{:key \"1:0\" :doc_count 10}{:key \"2:0\" :doc_count 100}]`

  becomes

  `{:1:0 10 :2:0 100}`"
  [buckets]
  (into {} (map #(assoc (dissoc % :key :doc_count) (keyword (:key %)) (:doc_count %)) buckets)))

(defn get-query
  "Get the Elasticsearch query from file."
  [query-file]
  (json/parse-string (slurp query-file) true))

(defn query
  "Query Elasticsearch and update the history-data map."
  [client config]
  (fn []
    (let [query (get-query (:query config))
          response (es/request client {:url (:url config "_search")
                                       :method :get
                                       :body query})
          buckets (get-in response [:body :aggregations :total_requests_per_day_hour :buckets])]
      (swap! history-data assoc (:name config :default) (transform buckets)))))

(defn history
  "Takes one parameter:

  `config`: A map containing the following properties
  
  `:name`       History name (default `:default`).
  `:connect`    Elasticsearch `:connect` (default `http://localhost:9200`).
  `:url`        Elasticsearch context (default `_search`)
  `:interval`   Interval for querying the `:source` (default `86400` seconds).
  `:query`      Path to the query file.

  "
  [config]
  (let [client (es/client {:hosts [(:connect config "http://localhost:9200")]})]
    (every! (:interval config 86400) 10 (query client config))))

