(ns riemann-history.elastic
  (:require
    [qbits.spandex :as es]
    [riemann.time :refer [every!]]
    [riemann-history.dev :as rhd]
    [clj-time.coerce :as clt-c]
    [clj-time.format :as clt-f]
    [clojure.core.async :as async]))

(def default-index "test-index")
(def default-type "_doc")
(def default-sample-recordcount 10000)

(defn generate-sample-data [index type n]
  (mapcat identity
    (take n
      (map (fn [id ts-last-week]
             [
               {:index {:_id id
                        :_index index       ;; Metadata Row of control json
                        :_type type}}      ;; https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html#docs-bulk
               { :timestamp ts-last-week            ;; (Dummy-)Event
                 :date (clt-f/unparse
                         (clt-f/formatters :mysql)
                         (clt-c/from-long ts-last-week))}])
          (map inc (range))
          (let [time-1w-millis (* 1000 24 7 60 60)
                time-now-millis (System/currentTimeMillis)]
               (repeat (- time-now-millis (long (rand time-1w-millis)))))))))

(defn bulk-request [bulk-request-data]
  (let [{:as ch
         :keys [input-ch output-ch]} (es/bulk-chan rhd/es-client {:flush-threshold 100
                                                                     :flush-interval 500
                                                                     :max-concurrent-requests 3})]
    (async/put! input-ch bulk-request-data)
    output-ch))

;; curl -XDELETE 'localhost:9200/<indexname>'
;; TODO use bulk request for wiping index
(defn wipe
  ([] (wipe [default-index]))
  ([index] (es/request rhd/es-client {:url (:index rhd/config index)
                                      :method :delete})))

(defn populate [& {:as param-map
                   :keys [index type record-count poll-result]
                   :or {index        default-index
                        type         default-type
                        record-count default-sample-recordcount
                        poll-result  :true}}]
  (let [bulk-out-chan (bulk-request (generate-sample-data index type record-count))]
    (println bulk-out-chan)
    (async/poll! bulk-out-chan)
    bulk-out-chan))

    ;(when poll-result
    ;  (async/go-loop []
    ;    (clojure.pprint/pprint (async/<! (:output-ch bulk-out-chan)))
    ;    (recur))))
  ;(println param-map index type record-count poll-result))

(comment
  (generate-sample-data "asdf" "_doc" 10)

  (defn populate-and-poll-result []
    (def bulk-out-chan (populate))
    (async/go-loop []
      (clojure.pprint/pprint (async/<! (:output-ch bulk-out-chan)))
      (recur)))

  (populate-and-poll-result))

(comment   ;; manual stuff
  (wipe)
  (def out-ch (populate))
  (def out-ch (bulk-request (generate-sample-data default-index default-type default-sample-recordcount)))
  (async/poll! out-ch))
