### How to create base line image

- create base line image nly if database already exits with data
- set property of base line = true 
- it will generate

- for schema if u made you version 0 as base line
- make a file V0 
- what i did was 
used pg something to dump all the schema into a file
cleaned it up using ai and put it into V0

do this before running the app
or crating base line cause in schme then 
flyway_schema_history also get added
