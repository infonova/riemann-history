(ns riemann-history.core-test
  (:require [riemann-history.core :as history]
            [clojure.test :refer :all]))

(deftest transform
  (is (= (history/transform [{:key "1:0" :doc_count 55} 
                             {:key "1:2" :doc_count 100} 
                             {:key "5:6" :doc_count 75}])
         {:1:0 55 :1:2 100 :5:6 75})))

(deftest epoch-tz-default
  (is (= (history/generate-key 1529932642.494)
         :1:13)))

(deftest epoch-tz-europe-vienna
  (is (= (history/generate-key 1529932642.494 "Europe/Vienna")
         :1:15)))
