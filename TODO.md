* split ES implementation into own clj file
* history-data structure with own key/impl: `{:exception-rate {...}  :orders {...}}`
* get history-data function. Do not access atom directly: `(get-history :exception-rate event)` 
* key gen fn from event time (epoch to Day:Hour key)
* recognize file ending when loading query (json/edn)
* write tests
