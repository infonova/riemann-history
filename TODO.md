* split ES implementation into own clj file
* recognize file ending when loading query (json/edn)
* write tests
* timezone issues: clt-time seems to work only with UTC, unparse-local for instance throws a ClassCastException
* idea: create an event in case of error/exception
