# Install Spring boot
```
mkdir api && cd api
curl https://start.spring.io/starter.tgz -d dependencies=web,data-jpa,quartz,devtools -d name=api | tar -xzvf -


# Install application
mvn clean package

# Check Quartz database
Quartz job is persisted into embedded database, you can access by h2database web console
```
http://localhost:8080/h2-console
jdbc:h2:mem:testdb
```


# Test

## Create a job

$ curl --header "Content-Type: application/json" -XPOST localhost:8080/scheduler/groups/report/jobs --data '{"name":"rep1","description":"unit report","schedules":[{"id":null,"start":"2018-05-31T00:00:00.000+0000","end":"2019-06-31T00:00:00.000+0000","suspended":false,"cron-expression":"0/10 * * * * ?","last-fired":null,"next-fire":null}],"scheduled":true,"class-name":"com.sungung.scheduler.api.service.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}' -i
HTTP/1.1 201
Location: http://localhost:8080/scheduler/groups/report/jobs/rep1
Content-Length: 0
Date: Fri, 01 Jun 2018 04:59:12 GMT


## Get a job detail

$ curl http://localhost:8080/scheduler/groups/report/jobs/rep1 -i
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 01 Jun 2018 04:59:41 GMT

{"name":"rep1","description":"unit report","schedules":[{"id":"rep1-93d972eb-22a7-4a51-abb3-e43d94bb0b4d","start":"2018-05-31T00:00:00.000+0000","end":"2019-07-01T00:00:00.000+0000","suspended":false,"cron-expression":"0/10 * * * * ?","last-fired":null,"next-fire":"2018-05-31T00:00:00.000+0000"}],"scheduled":true,"class-name":"com.sungung.scheduler.api.service.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}


## Delete job

$ curl -XDELETE http://localhost:8080/scheduler/groups/report/jobs/rep1


## Get a job trigger

$ curl http://localhost:8080/scheduler/groups/report/jobs/rep1/triggers/rep1-93d972eb-22a7-4a51-abb3-e43d94bb0b4d -i
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Fri, 01 Jun 2018 05:00:20 GMT

{"id":"rep1-93d972eb-22a7-4a51-abb3-e43d94bb0b4d","start":"2018-05-31T00:00:00.000+0000","end":"2019-07-01T00:00:00.000+0000","suspended":false,"cron-expression":"0/10 * * * * ?","last-fired":"2018-06-01T05:00:20.000+0000","next-fire":"2018-06-01T05:00:30.000+0000"}

## Delete(Unscheduling) job trigger ()

$ curl -XDELETE http://localhost:8080/scheduler/groups/report/jobs/rep1/triggers/rep1-93d972eb-22a7-4a51-abb3-e43d94bb0b4d -i
HTTP/1.1 200
Content-Length: 0
Date: Fri, 01 Jun 2018 05:01:14 GMT


## Reschedule a job trigger

$ curl --header "Content-Type: application/json" -XPOST http://localhost:8080/scheduler/groups/report/jobs/rep1/triggers/rep1-72253885-4ccd-434f-afd5-5b7b782587cc --data '{"start":"2018-05-31T00:00:00.000+0000","end":"2019-06-31T00:00:00.000+0000","suspended":false,"cron-expression":"0/30 * * * * ?","last-fired":null,"next-fire":null}' -i


## Execute once

$ curl -XPOST http://localhost:8080/scheduler/groups/report/jobs/rep1/execute -i
HTTP/1.1 200
Content-Length: 0
Date: Fri, 01 Jun 2018 05:44:56 GMT


