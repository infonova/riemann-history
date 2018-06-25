* buckets transformation: `[{:key "1:0" :doc_count: 15}{:key "1:1" :doc_count: 100}]` to `{:1.0 15 :1:1 100}`
* split ES implementation into own clj file
* history-data structure with own key/impl: `{:exception-rate {...}  :orders {...}}`
* get history-data function. Do not access atom directly: `(get-history :exception-rate event)` 
* key gen fn from event time (epoch to Day:Hour key)
* recognize file ending when loading query (json/edn)
* write tests
