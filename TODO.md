* split ES implementation into own clj file. Look at defprotocol
* recognize file ending when loading query (json/edn)
* write tests
* idea: create an event in case of error/exception
* travis integration

## klaus' list
### done:
* smoke tested ES setup and querying with helper namespaces within /dev
* spec errors were facepalm category: spec.alpha complaining about "arg-list vector?" stuff: missing arg-list vector! 
* used echo service (blog post) to inspect my actual request
* learned some basics about try/catch and spec and the fact that eastwood would certainly help
* ~~spandex isnt fit for use of _bulk api yet:~~ 
    * ~~encode-body "fixes" the correct control json~~ 
    * ~~and i bet it would also require a \newline to be appended~~
* ~~look into https://kevinmarsh.com/2014/10/23/using-jq-to-import-json-into-elasticsearch.html~~
    * ~~there should be a better way for seeding data (request body format right now kinda sucks imho as it isn't even real json)~~
* ~~use http lib directly for seed-data management actually we could completely discard spandex as dep~~
* spandex provides quite nice ways for making bulk requests (README actually helps #rtfm)
* update project.clj with proper dev profile
* cleanup deps between namespaces (actually we have same abstraction problem in dev as we do have in actual production code)
* figure out how to explain ResponseExceptions to myself (atm i think it could be any response other than 2xx ??)
    * spandex provides resposne-handle arg on client creation and some utility function for interop and decoding data
    * the default-response handler returns an clojure.lang.ExceptionInfo having a data map: ```(.data e)```

### TODO:
* docstrings
* pair the last mile with bhois
* rebase/squash