(ns riemann-history.core
  (:require [clojure.tools.logging :as log]
            [clojure.walk :as walk]
            [cheshire.core :as json]
            [clj-time.core :as clt-core]
            [clj-time.coerce :as clt-coerce]
            [clj-time.format :as clt-format]
            [qbits.spandex :as es]
            [riemann.time :refer [every!]]))

(defonce history-data
  (atom {:default {}}))

(defn get-history-data
  "Get history from `history-data` atom for given history and event key."
  [hk ek]
  (get-in @history-data [hk ek]))

(defn get-key-formatter [tz]
  (clt-format/with-zone
    (clt-format/formatter "e:H")(clt-core/time-zone-for-id tz)))

(defn generate-key
  "Get history-data key from event epoch."
  ([epoch] (generate-key epoch "UTC"))
  ([epoch tz]
   (keyword (clt-format/unparse
              (get-key-formatter tz)
              (clt-coerce/from-long (long (* 1000 epoch)))))))

(defn transform
  "Transforms Elasticsearch buckets vector to map. E.g.

  `[{:key \"1:0\" :doc_count 10}{:key \"2:0\" :doc_count 100}]`

  becomes

  `{:1:0 10 :2:0 100}`"
  [buckets]
  (walk/keywordize-keys (into {} (map (juxt :key :doc_count) buckets))))

(defn get-query
  "Get the Elasticsearch query from file."
  [query-file]
  (json/parse-string (slurp query-file) true))

(defn query
  "Query Elasticsearch and update the history-data map."
  [client config]
  (fn []
    (let [query (get-query (:query config))
          response (es/request client (merge
                                        (dissoc (:es-options config) :hosts)
                                        {:url (get-in config [:es-options :url] "_search")
                                         :method :get
                                         :body query}))
          buckets (get-in response [:body :aggregations (:aggregation config :riemann-history) :buckets])]
      (swap! history-data assoc (:name config :default) ((:transform-fn config transform) buckets)))))

(defn history
  "Takes one parameter:

  `config`: A map containing the following properties

  `:name`           History name (default `:default`).
  `:es-options      Elasticsearch client options with the following defaults:
    `:hosts`        Elasticsearch hosts (default `http://localhost:9200`).
    `:url`          Elasticsearch url (default `_search`)
  `:interval`       Interval for querying Elasticsearch (default `86400` seconds).
  `:query`          Path to the query file.
  `:aggregation`    Name of the Elasticsearch aggregation (default `riemann-history`).
  `:transform-fn`   Optional transform function for the ES query resultset (default `default-transform`).

  "
  [config]
  (let [client (es/client {:hosts [(get-in config [:es-options :hosts] "http://localhost:9200")]})]
    (every! (:interval config 86400) 10 (query client config))))
