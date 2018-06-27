(ns riemann-history.dev
  (:require
            [qbits.spandex :as es]
            [riemann.time :refer [every!]]
            [riemann-history.core :as rhc]))

(def config {:name :requests-per-day-hour
             :connect "http://elasticsearch:9200"
             :url "_search"
             :interval 600
             :query "/etc/riemann-history/elasticsearch.json"})

(def es-client (es/client {:hosts [(:connect config "http://localhost:9200")]}))

(comment
  (es/request es-client {:url    (:url config "_search")
                         :method :get
                         :body   (rhc/get-query (:query config))}))