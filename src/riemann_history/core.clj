(ns riemann-history.core
  (:require [riemann.time :refer [every!]]))

(def history-data 
  (atom 0))

(defn query-job
  "tbd"
  []
  (fn []
    (swap! history-data inc)))

(defn history
  "Takes one parameter:

  `config`: A map containing the following properties
  
  `:source`     Source for historical data.
  `:interval`   Interval for querying the `:source` (default `86400` seconds).
  `:file-path`  Path to the query file.

  "
  [config]
  (every! (:interval config 86400) 10 (query-job)))

