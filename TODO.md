* split ES implementation into own clj file. Look at defprotocol
* recognize file ending when loading query (json/edn)
* write tests
* timezone issues: clt-time seems to work only with UTC, unparse-local for instance throws a ClassCastException. Look at (with-zone)
* idea: create an event in case of error/exception
