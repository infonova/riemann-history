(ns riemann-history.core
  (:require [clojure.tools.logging :as log]
            [qbits.spandex :as es]
            [riemann.time :refer [every!]]))

(def history-data 
  (atom {}))

(defn query-job
  "Query Elasticsearch and update the history-data map."
  [client]
  (fn []
    (let [response (es/request client {:url "_template"})
          resp-body (:body response)]
      (log/info resp-body)      
      (reset! history-data resp-body))))

(defn history
  "Takes one parameter:

  `config`: A map containing the following properties
  
  `:connect`    Elasticsearch `:connect` (default `http://localhost:9200`).
  `:interval`   Interval for querying the `:source` (default `86400` seconds).
  `:query`      Path to the query file.

  "
  [config]
  (let [client (es/client {:hosts [(:connect config "http://localhost:9200")]})]
    (every! (:interval config 86400) 10 (query-job client))))

